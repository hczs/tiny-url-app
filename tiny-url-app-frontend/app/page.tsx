import { UrlForm } from '@/components/UrlForm';

const Page = () => {
  return (
      <div className="flex flex-col items-center justify-center h-screen bg-gray-100">
          <h1 className="text-3xl font-bold text-center mb-6">程の短网址生成器</h1>
          <UrlForm/>

          <div className="text-center mt-12">
              <p className="text-sm text-gray-500">免责声明：此程序仅供测试演示使用，数据会定时删除。</p>
          </div>
      </div>
  );
};

export default Page;
