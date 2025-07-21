"use client";

import React, { useEffect, useState } from "react";
import { apiFetch } from "@/app/lib/backend/client";
import { useRouter } from "next/navigation";

export default function Page() {
  const router = useRouter();
  const [checkingLogin, setCheckingLogin] = useState(true);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);

  useEffect(() => {
    const checkLoggedIn = async () => {
      try {
        const res = await apiFetch("/api/v1/members/me");
        const role = res.data?.role;

        if (role === "ADMIN") {
          window.location.href = "/";
        } else if (role === "USER") {
          window.location.href = "/";
        } else {
          setCheckingLogin(false); // role이 이상하거나 없으면 그대로 페이지 노출
        }
      } catch (err) {
        setCheckingLogin(false); // 로그인 안 된 사용자 → 페이지 보여줌
      }
    };

    checkLoggedIn();
  }, [router]);

  if (checkingLogin) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-white">
        <div className="animate-spin rounded-full h-10 w-10 border-t-2 border-b-2 border-blue-500"></div>
        <span className="ml-4 text-gray-600">로그인 상태 확인 중...</span>
      </div>
    );
  }

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    const form = e.target as HTMLFormElement;
    const emailInput = form.elements.namedItem("email") as HTMLInputElement;
    const passwordInput = form.elements.namedItem("password") as HTMLInputElement;

    const email = emailInput.value.trim();
    const password = passwordInput.value.trim();

    if (emailInput.value.length === 0) {
      emailInput.focus();
      return setErrorMsg("이메일을 입력해주세요.");
    }

    if (passwordInput.value.length === 0) {
      passwordInput.focus();
      return setErrorMsg("비밀번호를 입력해주세요.");
    }

    // 로그인 기능

    const endpoint = "/api/v1/members/login";

    try {
      const res = await apiFetch(endpoint, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ email, password }),
      });

      const role = res.data?.item?.role;

      if(role == "ADMIN"){
        window.location.href = "/";
      }

      if(role == "USER"){
        window.location.href = "/";
      }

    } catch (error: any) {
      let userFriendlyMsg = "알 수 없는 오류가 발생했습니다.";

      switch (error.resultCode) {
        case "400-1":
          userFriendlyMsg = "이메일 형식의 입력이 아닙니다.";
          break;
        default:
          if (error.msg) userFriendlyMsg = error.msg;
          break;
      }

      setErrorMsg(` ${userFriendlyMsg}`);
    }
  };

  return (
    <div className="bg-image flex justify-center items-center min-h-screen bg-gray-100">
      <div className="w-full max-w-sm p-6 bg-white rounded-2xl shadow-lg">
        <h1 className="text-3xl font-bold text-center text-gray-800 mb-6">로그인</h1>

        {errorMsg && (
          <div className="mb-4 p-3 bg-red-100 text-red-700 rounded border border-red-400">
            {errorMsg}
          </div>
        )}

        <form className="flex flex-col gap-4" onSubmit={handleSubmit}>
          <input
            className="text-gray-800 border border-gray-300 p-3 rounded-lg"
            type="text"
            name="email"
            placeholder="이메일"
            autoFocus
            maxLength={50}
          />
          <input
            className="text-gray-800 border border-gray-300 p-3 rounded-lg"
            type="password"
            name="password"
            placeholder="비밀번호"
            maxLength={30}
          />
          <button
            className="bg-[#6b4f3b] text-white p-3 rounded-lg hover:bg-[#8c7051] transition-colors duration-200"
            type="submit"
          >
            로그인
          </button>
        </form>

        <p className="text-center text-gray-500 text-sm mt-4">
          계정이 없으신가요?{" "}
          <a href="/members/signup" className="text-[#8c7051] font-medium hover:underline">
            회원가입
          </a>
        </p>
      </div>
    </div>
  );
}
