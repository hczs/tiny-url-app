import { UrlForm } from '@/components/UrlForm';
import { Button } from "@/components/ui/button"
import Link from "next/link";
import {ExternalLink} from "lucide-react";



const Page = () => {
  return (
      <div>
          <div className="flex flex-col items-center justify-center h-screen bg-gray-100">
              <h1 className="text-3xl font-bold text-center mb-6">程の短网址生成器</h1>
              <UrlForm/>

              <div className="text-center mt-12">
                  <Button asChild variant="link" className="text-sm">
                      <Link href={"https://github.com/hczs/tiny-url-app"} target={"_blank"}>
                          GitHub: https://github.com/hczs/tiny-url-app
                          <ExternalLink />
                      </Link>
                  </Button>
                  <p className="text-sm text-gray-500">
                      免责声明：此程序仅供测试演示使用，数据会定时删除。
                  </p>
              </div>
          </div>

      </div>

  );
};

export default Page;
