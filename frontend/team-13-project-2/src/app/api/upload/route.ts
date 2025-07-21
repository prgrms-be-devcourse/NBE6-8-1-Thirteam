// src/app/api/upload/route.ts
import { NextRequest, NextResponse } from "next/server";
import fs from "fs/promises";
import path from "path";

// 이 API는 Edge Runtime이 아닌 Node.js에서 실행되도록 설정
export const config = {
  api: {
    bodyParser: false,
  },
  runtime: "nodejs",
};

export async function POST(req: NextRequest) {
  const formData = await req.formData();
  const file = formData.get("file") as File;

  if (!file) {
    return NextResponse.json({ error: "No file uploaded" }, { status: 400 });
  }

  const bytes = await file.arrayBuffer();
  const buffer = Buffer.from(bytes);

  const filePath = path.join(process.cwd(), "public", "images", file.name);
  await fs.writeFile(filePath, buffer);

  return NextResponse.json({ message: "File uploaded", imageUrl: `/images/${file.name}` });
}
