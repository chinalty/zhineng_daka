#!/bin/bash

echo "========================================"
echo "  智能打卡系统 - 后端启动脚本"
echo "========================================"
echo ""

# 检查 Java 环境
if ! command -v java &> /dev/null; then
    echo "[错误] 未检测到 Java 环境，请先安装 JDK 17 或更高版本"
    exit 1
fi

echo "[1/3] 检查 .env 文件..."
if [ ! -f ".env" ]; then
    echo "[警告] 未找到 .env 文件，将使用默认配置"
    echo "[提示] 请复制 .env.example 为 .env 并填入配置"
    read -p "按回车继续..."
fi

echo "[2/3] 清理并编译项目..."
./mvnw clean package -DskipTests
if [ $? -ne 0 ]; then
    echo "[错误] 编译失败，请检查代码"
    exit 1
fi

echo "[3/3] 启动后端服务..."
echo ""
echo "========================================"
echo "  后端服务启动中..."
echo "  访问地址: http://localhost:8080"
echo "  按 Ctrl+C 停止服务"
echo "========================================"
echo ""

java -jar target/SailTrack-0.0.1-SNAPSHOT.jar
