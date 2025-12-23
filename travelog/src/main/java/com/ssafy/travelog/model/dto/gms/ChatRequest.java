package com.ssafy.travelog.model.dto.gms;

import java.util.List;

public record ChatRequest(String model, List<Message> messages) {

    private static final String SYSTEM_PROMPT = """
            너는 대한민국 구석구석을 잘 아는 '국내 여행 전문 큐레이터'이다.
            
            [대원칙]
            1. **무조건 대한민국 국내 여행지**만 추천하라. (해외 여행지 절대 금지)
            2. 사용자의 '과거 기록(List)'을 분석하여 선호하는 스타일을 파악하되, 해외 기록이 있다면 한국의 유사한 명소로 치환하여 생각하라.
            3. 사용자의 '희망 테마(List)'를 **모두 충족하거나 조화롭게 반영**할 수 있는 곳을 우선 추천하라.
            
            [출력 형식]
            추천지 3곳을 아래 포맷에 맞춰 답변하라:
            
            1. 🚩 **[지역명] 구체적인 장소**
               - 이유: [사용자의 여러 기록/테마를 어떻게 반영했는지 설명]
               - 꿀팁: [맛집, 포토존, 또는 액티비티 1가지]
            
            (마지막엔 즐거운 여행을 기원하는 짧은 멘트)
            """;

    public static ChatRequest of(List<String> userHistories, List<String> userThemes) {

        // 리스트를 콤마로 연결 (예: ["제주", "부산"] -> "제주, 부산")
        String historyStr = (userHistories == null || userHistories.isEmpty())
                ? "없음"
                : String.join(", ", userHistories);

        String themeStr = (userThemes == null || userThemes.isEmpty())
                ? "자유롭게 추천"
                : String.join(", ", userThemes);

        // 사용자 질문 조립
        String combinedUserMessage = String.format(
                "내 과거 여행 기록들은 [%s]이고, 이번 여행에서 원하는 테마들은 [%s]야. 이 정보들을 종합해서 최고의 국내 여행지를 추천해줘.",
                historyStr, themeStr
        );

        return new ChatRequest("gpt-4o-mini",
                List.of(
                        new Message("system", SYSTEM_PROMPT),
                        new Message("user", combinedUserMessage)
                )
        );
    }
}