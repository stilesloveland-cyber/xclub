#!/bin/bash
set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

info() { echo -e "${GREEN}[INFO]${NC} $1"; }
warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
error() { echo -e "${RED}[ERROR]${NC} $1"; }

REPAIR_MODE=false
UNINSTALL_MODE=false

for arg in "$@"; do
    case $arg in
        --repair) REPAIR_MODE=true ;;
        --uninstall) UNINSTALL_MODE=true ;;
    esac
done

if [ "$UNINSTALL_MODE" = true ]; then
    info "正在卸载 Xclub Sync Server..."
    docker compose down 2>/dev/null || true
    read -p "是否删除数据？[y/N] " -r
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        rm -rf ./data
        docker volume rm xclub_caddy_data xclub_caddy_config 2>/dev/null || true
        info "数据已删除"
    else
        info "数据已保留"
    fi
    docker rmi xclub-sync-server 2>/dev/null || true
    info "卸载完成"
    exit 0
fi

if [ "$REPAIR_MODE" = true ]; then
    info "正在修复 Xclub Sync Server..."
    if ! docker compose ps | grep -q "sync-server.*running"; then
        warn "容器未运行，尝试重启..."
        docker compose up -d
    fi
    if ! nc -z localhost "${PORT:-5555}" 2>/dev/null; then
        warn "端口 ${PORT:-5555} 不可达，检查容器日志..."
        docker compose logs --tail=20 sync-server
    fi
    if [ -d "./data" ]; then
        if [ -f "./data/xclub_sync.db" ]; then
            info "数据库文件存在"
        else
            warn "数据库文件缺失，将在下次启动时重建"
        fi
    fi
    info "修复完成"
    exit 0
fi

info "=== Xclub Sync Server 部署向导 ==="

if ! command -v docker &> /dev/null; then
    error "Docker 未安装"
    echo "请先安装 Docker: https://docs.docker.com/get-docker/"
    exit 1
fi

if ! docker compose version &> /dev/null; then
    error "Docker Compose 未安装"
    exit 1
fi

info "Docker 环境检查通过"

read -p "端口号 [默认 5555]: " PORT_INPUT
PORT="${PORT_INPUT:-5555}"

read -p "是否有域名？[y/N] " -r HAS_DOMAIN
DOMAIN=""
if [[ $REPLY =~ ^[Yy]$ ]]; then
    read -p "请输入域名: " DOMAIN
fi

USE_CLOUDFLARE=false
CLOUDFLARE_TUNNEL_TOKEN=""
if [ -n "$DOMAIN" ]; then
    read -p "是否使用 Cloudflare？[y/N] " -r
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        USE_CLOUDFLARE=true
        read -p "Cloudflare Tunnel Token: " CLOUDFLARE_TUNNEL_TOKEN
    fi
fi

read -p "数据存储路径 [默认 ./data]: " DATA_PATH
DATA_PATH="${DATA_PATH:-./data}"

mkdir -p "$DATA_PATH"

cat > .env << EOF
PORT=$PORT
DOMAIN=$DOMAIN
CLOUDFLARE_TUNNEL_TOKEN=$CLOUDFLARE_TUNNEL_TOKEN
EOF

echo ""
info "=== 配置摘要 ==="
echo "  端口: $PORT"
echo "  域名: ${DOMAIN:-无（使用 IP 访问）}"
echo "  Cloudflare: $USE_CLOUDFLARE"
echo "  数据路径: $DATA_PATH"
echo ""

read -p "确认部署？[Y/n] " -r
if [[ $REPLY =~ ^[Nn]$ ]]; then
    warn "部署已取消"
    exit 0
fi

info "正在构建镜像..."
docker compose build

if [ "$USE_CLOUDFLARE" = true ]; then
    info "启动服务（含 Cloudflare Tunnel）..."
    docker compose --profile cloudflare up -d
else
    info "启动服务..."
    docker compose up -d
fi

info "等待服务启动..."
sleep 5

if nc -z localhost "$PORT" 2>/dev/null; then
    info "✅ 部署成功！"
    if [ -n "$DOMAIN" ]; then
        echo "  访问地址: https://$DOMAIN"
    else
        echo "  访问地址: http://localhost:$PORT"
    fi
    echo "  健康检查: http://localhost:$PORT/health"
else
    warn "端口 $PORT 暂时不可达，请检查日志: docker compose logs"
fi

echo ""
echo "管理命令:"
echo "  修复: bash deploy.sh --repair"
echo "  卸载: bash deploy.sh --uninstall"
