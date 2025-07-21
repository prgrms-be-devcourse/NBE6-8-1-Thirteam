'use client';

import React, { useEffect, useState } from "react";
import { apiFetch } from "@/app/lib/backend/client";
import { useRouter } from "next/navigation";

export default function AuthNav() {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [userName, setUserName] = useState("");
    const router = useRouter();

    useEffect(() => {
        apiFetch("/api/v1/members/me")
            .then((res) => {
                if (res.resultCode.startsWith("202")) {
                    setIsLoggedIn(true);
                    setUserName(res.data.name);
                }
            })
            .catch(() => {
                setIsLoggedIn(false);
                setUserName("");
            });
    }, []);

    const handleLogout = async () => {
        await apiFetch("/api/v1/members/logout", { method: "POST" });
        setIsLoggedIn(false);
        setUserName("");
        window.location.href = "/";
    };

    if (isLoggedIn) {
        return (
            <div className="flex items-center gap-2 cursor-pointer">
                {/* 사용자 아이콘을 클릭하면 /user/list로 이동 */}
                <div
                    onClick={() => router.push("/user")}
                    className="w-8 h-8 bg-[#8c7051] rounded-full flex items-center justify-center text-white text-sm cursor-pointer"
                >
                    {userName.charAt(0).toUpperCase()}
                </div>

                {/* 사용자 이름을 클릭하면 /user/list로 이동 */}
                <span
                    onClick={() => router.push("/user")}
                    className="text-white cursor-pointer hover:text-[#8c7051] transition-colors"
                >
                    {userName}
                </span>

                {/* 로그아웃 버튼 */}
                <button
                    onClick={handleLogout}
                    className="ml-4 text-white px-3 py-2 rounded-lg hover:bg-[#8c7051] transition-colors"
                >
                    로그아웃
                </button>
            </div>
        );
    }

    return (
        <div className="text-white hover:bg-[#8c7051] px-3 py-2 rounded-lg transition-colors cursor-pointer" onClick={() => router.push('/members/login')}>
            로그인
        </div>
    );
}
