import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";

import "./globals.css";
import "react-toastify/dist/ReactToastify.css";

import Header from "@/components/Header";
import { CartProvider } from "@/app/contexts/CartContext";
import { ToastContainer } from "react-toastify";

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "Grids & Circles",
  description: "카페 메뉴 관리 서비스",
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="ko">
      <body
        className={`${geistSans.variable} ${geistMono.variable} antialiased min-h-screen`}
      >
        <CartProvider>
          <Header /> {/* 네비게이션 + AuthNav 포함 */}
          <ToastContainer position="top-center" autoClose={2000} />
          <main className="flex-1">{children}</main>
          <footer className="bg-[#d9c1a3] text-[#6b4f3b] text-center py-4">
            <p className="font-medium">© 2025 Grids & Circles</p>
          </footer>
        </CartProvider>
      </body>
    </html>
  );
}