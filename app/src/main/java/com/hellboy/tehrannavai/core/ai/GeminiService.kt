package com.hellboy.tehrannavai.core.ai

import com.google.ai.client.generativeai.GenerativeModel

class GeminiService(private val apiKey: String) {
    // از مدل ارزان‌تر و سریع‌تر flash استفاده می‌کنیم
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey
    )

    suspend fun extractDestinationFromPrompt(userPrompt: String): String {
        val systemInstruction = """
            شما یک دستیار هوشمند مسیریابی در تهران هستید.
            کاربر درخواستی به زبان طبیعی می‌دهد (مثلا 'کجا قهوه بخورم' یا 'میخوام برم فرودگاه مهرآباد').
            وظیفه شما این است که فقط و فقط نام کوتاه و دقیق مکان را برای جستجو در گوگل مپ برگردانید.
            هیچ کلمه اضافه‌ای چاپ نکنید.
            مثال ورودی: نزدیک ترین پمپ بنزین کجاست؟
            مثال خروجی: پمپ بنزین
            
            مثال ورودی: منو ببر بیمارستان میلاد
            مثال خروجی: بیمارستان میلاد
            
            درخواست کاربر: $userPrompt
        """.trimIndent()

        val response = generativeModel.generateContent(systemInstruction)
        return response.text?.trim() ?: ""
    }
}
