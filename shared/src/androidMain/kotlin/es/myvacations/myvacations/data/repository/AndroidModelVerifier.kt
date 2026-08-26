package es.myvacations.myvacations.data.repository

import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import ai.onnxruntime.extensions.OrtxPackage
import es.myvacations.myvacations.core.utils.AndroidContextHolder
import es.myvacations.myvacations.data.datasource.local.ModelFiles
import es.myvacations.myvacations.data.datasource.remote.ModelVerifier
import java.io.File

class AndroidModelVerifier : ModelVerifier {
    private val environment by lazy {
        OrtEnvironment.getEnvironment()
    }

    private val modelPath = File(
        AndroidContextHolder.context.filesDir,
        "${ModelFiles.DIRECTORY}/${ModelFiles.MODEL}"
    ).absolutePath

    private val tokenizerPath = File(
        AndroidContextHolder.context.filesDir,
        "${ModelFiles.DIRECTORY}/${ModelFiles.TOKENIZER}"
    ).absolutePath

    override suspend fun verify() {
        val options = OrtSession.SessionOptions()
        options.registerCustomOpLibrary(OrtxPackage.getLibraryPath())

        environment.createSession(modelPath).close()
        environment.createSession(tokenizerPath, options).close()
    }
}