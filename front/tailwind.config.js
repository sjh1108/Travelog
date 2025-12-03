/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,jsx,vue,ts,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        background: "#ffffff", // 원하는 배경색 코드 (예: 흰색)
        // 만약 'foreground' 같은 다른 에러도 난다면 여기에 추가
      },
      animation: {
        "accordion-down": "accordion-down 0.2s ease-out",
        "accordion-up": "accordion-up 0.2s ease-out",
      },
      keyframes: {
        "accordion-down": {
          from: { height: 0 },
          to: { height: "var(--radix-accordion-content-height)" },
        },
        "accordion-up": {
          from: { height: "var(--radix-accordion-content-height)" },
          to: { height: 0 },
        },
      },
    },
  },
  plugins: [
    require("tailwindcss-animate"),
  ],
}
