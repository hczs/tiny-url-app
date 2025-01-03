/** @type {import('next').NextConfig} */
const nextConfig = {
    async rewrites() {
        return [
            {
                source: '/api/:path*', // 匹配所有以/api开头的请求
                destination: 'http://localhost:8080/api/:path*', // 将其转发到本地后端服务
            },
        ].filter(Boolean); // 确保只返回有效的重写规则
    },
};

export default nextConfig;