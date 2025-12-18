@echo off
chcp 65001 >nul
echo ========================================
echo   智能打卡系统 - 前端启动脚本
echo ========================================
echo.

REM 检查 Node.js 环境
node -v >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未检测到 Node.js 环境，请先安装 Node.js 16 或更高版本
    pause
    exit /b 1
)

echo [1/3] 检查 .env 文件...
if not exist ".env" (
    echo [警告] 未找到 .env 文件，将使用默认配置
    echo [提示] 请复制 .env.example 为 .env 并填入配置
    pause
)

echo [2/3] 安装依赖...
if not exist "node_modules" (
    echo 首次运行，正在安装依赖...
    call npm install
    if %errorlevel% neq 0 (
        echo [错误] 依赖安装失败
        pause
        exit /b 1
    )
) else (
    echo 依赖已安装，跳过安装步骤
)

echo [3/3] 启动开发服务器...
echo.
echo ========================================
echo   前端服务启动中...
echo   访问地址: http://localhost:3000
echo   按 Ctrl+C 停止服务
echo ========================================
echo.

call npm run dev

pause
