package es.myvacations.myvacations.data.repository

import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import ai.onnxruntime.extensions.OrtxPackage
import es.myvacations.myvacations.core.utils.AndroidContextHolder
import es.myvacations.myvacations.data.datasource.remote.ModelVerifier
import io.github.aakira.napier.Napier
import java.io.File

class AndroidModelVerifier : ModelVerifier {
    private val environment by lazy {
        OrtEnvironment.getEnvironment()
    }

    private val modelPath = File(
        AndroidContextHolder.context.filesDir,
        "ai/model_quantized.onnx"
    ).absolutePath

    private val tokenizerPath = File(
        AndroidContextHolder.context.filesDir,
        "ai/tokenizer.onnx"
    ).absolutePath

    override suspend fun verify() {
        Napier.d(tag = "FILES", message = "filesDir = ${AndroidContextHolder.context.filesDir.absolutePath}")

        AndroidContextHolder.context.filesDir.walkTopDown().forEach {
            Napier.d(tag = "FILES", message = "${it.absolutePath} (${it.length()})")
        }
        val options = OrtSession.SessionOptions()
        options.registerCustomOpLibrary(OrtxPackage.getLibraryPath())

        environment.createSession(modelPath).close()
        environment.createSession(tokenizerPath, options).close()
    }
}