"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { apiFetch } from "@/app/lib/backend/client";


export default function HomePage() {
  const [checking, setChecking] = useState(true);
  const [userRole, setUserRole] = useState<"USER" | "ADMIN" | null>(null);
  const [userName, setUserName] = useState("");
  const router = useRouter();

  useEffect(() => {
    const checkLogin = async () => {
      try {
        const res = await apiFetch("/api/v1/members/me", {
          headers: {
          "Cache-Control": "no-store",
          },
      });
        const role = res.data?.role;
        const name = res.data?.name;

        if (role === "USER" || role === "ADMIN") {
          setUserRole(role);
          setUserName(name);
        }
      } catch (err) {
        // 로그인 안 되어 있음
      } finally {
        setChecking(false);
      }
    };

    checkLogin();
  }, []);

  const goToMyPage = () => {
    if (userRole === "ADMIN") router.push("/admin");
    else if (userRole === "USER") router.push("/user");
  };

  const handleLogout = async () => {
    await apiFetch("/api/v1/members/logout", { method: "POST" });
    setUserRole(null);
    setUserName("");
    window.location.href = "/";
  };

  if (checking) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-white">
        <div className="animate-spin rounded-full h-10 w-10 border-t-2 border-b-2 border-blue-500"></div>
        <span className="ml-4 text-gray-600">로그인 상태 확인 중...</span>
      </div>
    );
  }

  return (
    <div className="bg-image flex flex-col items-center justify-center min-h-screen text-center bg-[#f7f3ef]">
      <div className="bg-white p-8 rounded-2xl shadow-lg max-w-lg">
        <h1 className="text-4xl font-bold text-[#6b4f3b] mb-4">Grids & Circles</h1>
        <p className="text-[#8c7051] text-lg mb-6">언제나 최고의 커피를 제공합니다.</p>

        {userRole ? (
          <>
          <button
            onClick={goToMyPage}
            className="w-full flex items-center justify-center gap-3 bg-[#6b4f3b] text-white py-3 px-5 rounded-lg hover:bg-[#8c7051] transition-colors mb-4"
          >
            <div className="w-8 h-8 bg-[#8c7051] rounded-lg flex items-center justify-center font-semibold">
              {userName.charAt(0).toUpperCase()}
            </div>
            <span className="font-medium">{userName}님, 환영합니다.</span>
          </button>
          <button
              onClick={handleLogout}
              className="text-[#6b4f3b] border border-[#6b4f3b] py-3 px-5 rounded-lg hover:bg-[#d9c1a3] hover:text-white transition-colors w-full max-w-xs"
            >
              로그아웃
            </button>
          </>
        ) : (
          <div className="flex flex-col">
            <button
              onClick={() => router.push("/members/login")}
              className="bg-[#6b4f3b] text-white py-3 rounded-lg hover:bg-[#8c7051] transition-colors mb-4"
            >
              로그인
            </button>
            <button
              onClick={() => router.push("/members/signup")}
              className="border border-[#6b4f3b] text-[#6b4f3b] py-3 rounded-lg hover:bg-[#d9c1a3] hover:text-white transition-colors"
            >
              회원가입
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
