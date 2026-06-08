import express from "express";
import { GoogleGenerativeAI } from "@google/generative-ai";

const app = express();
const genAI = new GoogleGenerativeAI(process.env.GEMINI_API_KEY || "");

app.use(express.json({ limit: "10mb" }));

app.post("/analyze", async (req, res) => {
    console.log("[요청 수신]");
    try {
        const b64: string = req.body.image;
        const model = genAI.getGenerativeModel({ model: "gemini-1.5-flash" });

        const result = await model.generateContent([
            {
                inlineData: {
                    mimeType: "image/jpeg",
                    data: b64
                }
            },
            "너는 그래픽 에디터 'Null Canvas'의 AI 어시스턴트야. 캔버스 스크린샷을 보고 한국어로 짧게(2문장 이내) 조언해. 도형 배치, 색상, 구도 등 구체적으로."
        ]);

        const text = result.response.text();
        res.json({ advice: text });

    } catch (err: any) {
        res.status(500).json({ advice: "분석 실패: " + err.message });
    }
});

app.get("/health", (_, res) => res.json({ status: "ok" }));
const PORT = 8000;
app.listen(PORT, () => console.log(`[Supporter] http://localhost:${PORT}`));

