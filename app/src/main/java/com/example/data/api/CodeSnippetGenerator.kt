package com.example.data.api

import com.example.data.model.ChatApiMessage

object CodeSnippetGenerator {

    fun generateChatCompletionsPython(
        model: String,
        messages: List<ChatApiMessage>,
        temperature: Double,
        topP: Double,
        maxTokens: Int,
        stream: Boolean = false
    ): String {
        val messagesPy = messages.joinToString(separator = ",\n        ") { msg ->
            val escaped = msg.content.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
            """{"role": "${msg.role}", "content": "$escaped"}"""
        }

        return if (stream) {
            """
from openai import OpenAI

# Initialize client (uses OPENAI_API_KEY environment variable)
client = OpenAI()

response = client.chat.completions.create(
    model="$model",
    messages=[
        $messagesPy
    ],
    temperature=$temperature,
    top_p=$topP,
    max_tokens=$maxTokens,
    stream=True,
)

# Stream chunks in real-time
for chunk in response:
    content = chunk.choices[0].delta.content or ""
    print(content, end="", flush=True)
print()
            """.trimIndent()
        } else {
            """
from openai import OpenAI

# Initialize client (uses OPENAI_API_KEY environment variable)
client = OpenAI()

completion = client.chat.completions.create(
    model="$model",
    messages=[
        $messagesPy
    ],
    temperature=$temperature,
    top_p=$topP,
    max_tokens=$maxTokens,
)

print(completion.choices[0].message.content)
            """.trimIndent()
        }
    }

    fun generateImageDallePython(
        prompt: String,
        model: String = "dall-e-3",
        size: String = "1024x1024",
        quality: String = "standard",
        style: String = "vivid",
        n: Int = 1
    ): String {
        val escapedPrompt = prompt.replace("\\", "\\\\").replace("\"", "\\\"")
        return """
from openai import OpenAI

client = OpenAI()

response = client.images.generate(
    model="$model",
    prompt="$escapedPrompt",
    size="$size",
    quality="$quality",
    style="$style",
    n=$n,
)

image_url = response.data[0].url
print(f"Generated Image URL: {image_url}")
if hasattr(response.data[0], "revised_prompt"):
    print(f"Revised Prompt: {response.data[0].revised_prompt}")
        """.trimIndent()
    }

    fun generateTtsPython(
        input: String,
        voice: String = "alloy",
        model: String = "tts-1",
        speed: Double = 1.0,
        outputFilename: String = "speech.mp3"
    ): String {
        val escaped = input.replace("\\", "\\\\").replace("\"", "\\\"")
        return """
from openai import OpenAI

client = OpenAI()

response = client.audio.speech.create(
    model="$model",
    voice="$voice",
    input="$escaped",
    speed=$speed,
)

# Stream binary audio data directly to file
response.stream_to_file("$outputFilename")
print(f"Audio saved to {outputFilename}")
        """.trimIndent()
    }

    fun generateWhisperPython(
        filePath: String = "audio_recording.mp3",
        language: String = "en"
    ): String {
        return """
from openai import OpenAI

client = OpenAI()

with open("$filePath", "rb") as audio_file:
    transcription = client.audio.transcriptions.create(
        model="whisper-1",
        file=audio_file,
        language="$language",
        response_format="verbose_json",
        timestamp_granularities=["word", "segment"]
    )

print(transcription.text)
        """.trimIndent()
    }

    fun generateEmbeddingsPython(
        input: String = "OpenAI SDK provides Pythonic bindings for all API endpoints",
        model: String = "text-embedding-3-small"
    ): String {
        val escaped = input.replace("\\", "\\\\").replace("\"", "\\\"")
        return """
from openai import OpenAI

client = OpenAI()

response = client.embeddings.create(
    model="$model",
    input="$escaped",
    encoding_format="float"
)

vector = response.data[0].embedding
print(f"Vector Dimensions: {len(vector)}")
print(f"Sample preview: {vector[:5]}...")
        """.trimIndent()
    }

    fun generateModelsListPython(): String {
        return """
from openai import OpenAI

client = OpenAI()

# List all available models
models = client.models.list()

for model in sorted(models.data, key=lambda m: m.id):
    print(f"Model ID: {model.id} (Owner: {model.owned_by})")
        """.trimIndent()
    }
}
