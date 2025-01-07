/** @type {import('next').NextConfig} */

const nextConfig = {
    async rewrites() {
        return [
            // 优先匹配 /api 接口
            {
                source: '/api/:path*',
                destination: (process.env.NEXT_PUBLIC_API_PROXY_URL || 'http://localhost:8080') + '/api/:path*' ,
            },
            // 根目录自动走短链跳转
            {
                source: '/:path*',
                destination: (process.env.NEXT_PUBLIC_API_PROXY_URL || 'http://localhost:8080') + '/api/v1/:path*',
            },
        ].filter(Boolean);
    },
};

export default nextConfig;