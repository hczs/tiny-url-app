'use client';

import {useState} from 'react';
import {Button} from "@/components/ui/button"
import {Input} from "@/components/ui/input"
import {Label} from "@/components/ui/label"
import {useToast} from "@/hooks/use-toast"
import {Toaster} from "@/components/ui/toaster"

const UrlForm = () => {
    const [longUrl, setLongUrl] = useState('');
    const [shortUrl, setShortUrl] = useState('');
    const [loading, setLoading] = useState(false);
    const {toast} = useToast()


    const generateShortUrl = async () => {
        try {
            setLoading(true);
            const response = await fetch('/api/v1/data/shorten', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({longUrl: longUrl}),
            });
            if (response.ok) {
                const data = await response.json();
                setShortUrl(window.location.href + "api/v1/" + data.shortUrl);
                toast({
                    description: "成功生成短网址！",
                });
            } else {
                toast({
                    variant: "destructive",
                    description: "响应异常，状态码：" + response.status,
                });
            }
        } catch (error) {
            console.error('Error generating short URL:', error);
            toast({
                variant: "destructive",
                description: "服务异常，生成短网址失败！",
            });
        } finally {
            setLoading(false);
        }

    };

    return (
        <div className="max-w-md w-full bg-white p-6 rounded-lg shadow-lg">
            <Toaster/>
            <Label htmlFor="longUrl" className="text-lg font-medium mb-2">请输入长网址</Label>
            <Input
                id="longUrl"
                type="url"
                value={longUrl}
                onChange={(e) => setLongUrl(e.target.value)}
                className="mb-4 p-2 w-full border border-gray-300 rounded"
                placeholder="https://example.com"
            />
            <Button loading={loading} onClick={generateShortUrl} disabled={!longUrl} className="w-full">
                {loading ? <span>短网址生成中，请等待...</span> : <span>生成短网址</span>}
            </Button>

            {shortUrl && (
                <div className="mt-4">
                    <Label className="text-lg font-medium mb-2">生成的短网址</Label>
                    <div className="flex items-center justify-between bg-gray-100 p-3 rounded">
                        <span>{shortUrl}</span>
                        <Button onClick={() => {
                            navigator.clipboard.writeText(shortUrl)
                            toast({
                                description: "已复制到剪切板",
                            })
                        }} className="ml-4">
                            复制
                        </Button>
                    </div>
                </div>
            )}
        </div>
    );
};

export {UrlForm};
