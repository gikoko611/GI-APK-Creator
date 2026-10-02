package com.gi.apkcreator

import android.os.Bundle
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.animation.core.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.foundation.text.selection.SelectionContainer
import kotlinx.coroutines.delay
import com.gi.apkcreator.data.ProjectStore
import com.gi.apkcreator.model.Project

private val Bg = Color(0xFF05070A)
private val Panel = Color(0xFF0B1016)
private val Panel2 = Color(0xFF101A18)
private val Green = Color(0xFF00FF88)
private val GreenSoft = Color(0xFF19C77A)
private val GreenDark = Color(0xFF063D29)
private val Border = Color(0xFF124D36)
private val TextMain = Color(0xFFD7FFE9)
private val Muted = Color(0xFF6D9582)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = android.graphics.Color.rgb(5, 7, 10)
        window.navigationBarColor = android.graphics.Color.rgb(5, 7, 10)

        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Bg
                ) {
                    GIApkCreator()
                }
            }
        }
    }
}

@Composable
fun GIApkCreator() {

    val context = LocalContext.current
    val projectStore = remember { ProjectStore(context) }

    var showSplash by remember { mutableStateOf(true) }

    var projects by remember {
        mutableStateOf(projectStore.getProjects())
    }

    var selectedProjectId by remember {
        mutableStateOf<String?>(null)
    }

    var prompt by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("CREATE") }
    var buildScreen by remember { mutableStateOf(false) }
    var buildRunning by remember { mutableStateOf(false) }
    var buildProgress by remember { mutableFloatStateOf(0f) }
    var buildStatus by remember { mutableStateOf("READY") }
    var errorMessage by remember { mutableStateOf("") }
    var buildLogs by remember {
        mutableStateOf(
            listOf(
                "> G.I build system ready",
                "> cloud build available"
            )
        )
    }

    LaunchedEffect(Unit) {
        delay(1800)
        showSplash = false
    }

    if (showSplash) {
        GISplashScreen()
        return
    }

    fun startBuild(promptOverride: String? = null) {

        buildScreen = true
        errorMessage = ""

        val buildPrompt = promptOverride ?: prompt

        if (buildPrompt.trim().isEmpty()) {
            buildRunning = false
            buildProgress = 0f
            buildStatus = "FAILED"

            errorMessage =
                "Application prompt is empty. Describe the app you want to build."

            buildLogs = listOf(
                "> validating project",
                "> ERROR: empty application prompt",
                "> build stopped"
            )

            return
        }

        buildRunning = true
        buildProgress = 0f
        buildStatus = "PREPARING"

        buildLogs = listOf(
            "> initializing G.I build system",
            "> checking project configuration"
        )
    }

    fun cancelBuild() {
        buildRunning = false
        buildStatus = "CANCELLED"
        buildLogs = buildLogs + "> build cancelled by user"
    }

    LaunchedEffect(buildRunning) {

        if (!buildRunning) return@LaunchedEffect

        val steps = listOf(
            8 to "creating Android project",
            16 to "generating project files",
            25 to "checking dependencies",
            36 to "running Gradle",
            48 to ":app:preBuild",
            59 to ":app:compileDebugKotlin",
            68 to ":app:mergeDebugResources",
            77 to ":app:processDebugResources",
            86 to ":app:compileDebugJavaWithJavac",
            93 to ":app:packageDebug",
            98 to "finalizing APK",
            100 to "build completed"
        )

        for ((progress, message) in steps) {

            if (!buildRunning) break

            delay(450)

            buildProgress = progress / 100f

            buildStatus = when {
                progress < 30 -> "GENERATING"
                progress < 90 -> "BUILDING"
                progress < 100 -> "PACKAGING"
                else -> "SUCCESS"
            }

            buildLogs = (buildLogs + "> $message").takeLast(18)
        }

        if (buildRunning) {
            buildRunning = false
            buildStatus = "SUCCESS"
            buildProgress = 1f
            buildLogs = (buildLogs + "> APK ready").takeLast(18)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
    ) {

        Header(
            onMenuClick = {
                selectedTab = "CREATE"
                buildScreen = false
            },
            onSettingsClick = {
                selectedTab = "SETTINGS"
                buildScreen = false
            }
        )

        if (buildScreen) {

            BuildScreen(
                progress = buildProgress,
                status = buildStatus,
                logs = buildLogs,
                errorMessage = errorMessage,
                running = buildRunning,
                onCancel = { cancelBuild() },
                onRetry = { startBuild() },
                onBack = {
                    buildScreen = false
                }
            )

        } else {

            when (selectedTab) {

                "DETAIL" -> {
                    val project = selectedProjectId?.let {
                        projectStore.getProject(it)
                    }

                    if (project != null) {
                        ProjectDetailScreen(
                            project = project,
                            onBack = {
                                selectedTab = "PROJECTS"
                            },
                            onEdit = {
                                prompt = project.prompt
                                selectedTab = "CREATE"
                            },
                            onBuild = {
                                prompt = project.prompt
                                startBuild(project.prompt)
                            },
                            onDelete = {
                                projectStore.deleteProject(project.id)
                                projects = projectStore.getProjects()
                                selectedProjectId = null
                                selectedTab = "PROJECTS"
                            }
                        )
                    } else {
                        selectedTab = "PROJECTS"
                    }
                }

                "CREATE" -> CreateScreen(
                    prompt = prompt,
                    projects = projects,
                    onPromptChange = { prompt = it },
                    onGenerate = {
                        if (prompt.trim().isNotEmpty()) {
                            val now = System.currentTimeMillis()

                            val project = Project(
                                id = now.toString(),
                                name = prompt
                                    .trim()
                                    .split("\\s+".toRegex())
                                    .take(4)
                                    .joinToString(" ")
                                    .ifBlank { "New Project" },
                                description = "Android • Compose",
                                prompt = prompt.trim(),
                                template = "CUSTOM",
                                createdAt = now,
                                updatedAt = now
                            )

                            projectStore.saveProject(project)
                            projects = projectStore.getProjects()
                            selectedProjectId = project.id
                        }

                        startBuild()
                    },
                    onProjectClick = {
                        selectedTab = "PROJECTS"
                    },
                    onSavedProjectClick = { project ->
                        selectedProjectId = project.id
                        selectedTab = "DETAIL"
                    }
                )

                "PROJECTS" -> ProjectsScreen(
                    projects = projects,
                    onProjectClick = { project ->
                        selectedProjectId = project.id
                        selectedTab = "DETAIL"
                    }
                )

                "SETTINGS" -> SettingsScreen()
            }

            Spacer(Modifier.weight(1f))

            BottomNavigation(
                selected = selectedTab,
                onSelected = {
                    selectedTab = it
                }
            )
        }
    }
}

@Composable
fun Header(
    onMenuClick: () -> Unit,
    onSettingsClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Panel)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = "☰",
                color = Green,
                fontSize = 21.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.clickable {
                    onMenuClick()
                }
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "G.I APK CREATOR",
                    color = Green,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                Text(
                    text = "AI APP BUILDER",
                    color = Muted,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Text(
                text = "⚙",
                color = Green,
                fontSize = 20.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.clickable {
                    onSettingsClick()
                }
            )
        }
    }

    DividerLine()
}

@Composable
fun CreateScreen(
    prompt: String,
    projects: List<Project>,
    onPromptChange: (String) -> Unit,
    onGenerate: () -> Unit,
    onProjectClick: () -> Unit,
    onSavedProjectClick: (Project) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(13.dp)
    ) {

        SectionTitle("CREATE NEW APP")

        TerminalInput(
            value = prompt,
            onValueChange = onPromptChange
        )

        GreenButton(
            text = "✦  GENERATE APP",
            onClick = onGenerate
        )

        SectionTitle("QUICK TEMPLATES")

        TemplateGrid(
            onSelected = onPromptChange
        )

        SectionTitle("YOUR PROJECTS")

        if (projects.isEmpty()) {
            TerminalPanel {
                Text(
                    text = "> no saved projects",
                    color = Muted,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp
                )

                Text(
                    text = "> create your first app above",
                    color = Muted,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp
                )
            }
        } else {
            projects.take(5).forEach { project ->
                ProjectCard(
                    name = project.name,
                    description = project.description,
                    onClick = {
                        onSavedProjectClick(project)
                    }
                )
            }
        }

        TerminalPanel {

            Text(
                text = "G.I AI :: READY",
                color = Green,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp
            )

            Text(
                text = "> cloud build available",
                color = Muted,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp
            )

            Text(
                text = "> build logs enabled",
                color = Muted,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp
            )
        }

        Spacer(Modifier.height(10.dp))
    }
}

@Composable
fun TerminalInput(
    value: String,
    onValueChange: (String) -> Unit
) {

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 150.dp),
        textStyle = LocalTextStyle.current.copy(
            color = TextMain,
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp
        ),
        placeholder = {
            Text(
                text = "> describe your application...\n\ncreate a notes app with dark mode",
                color = Muted,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp
            )
        },
        label = {
            Text(
                text = "$ prompt",
                color = Green,
                fontFamily = FontFamily.Monospace
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Green,
            unfocusedBorderColor = Border,
            focusedLabelColor = Green,
            unfocusedLabelColor = Muted,
            cursorColor = Green,
            focusedContainerColor = Panel,
            unfocusedContainerColor = Panel
        ),
        shape = RoundedCornerShape(8.dp)
    )
}

@Composable
fun TemplateGrid(
    onSelected: (String) -> Unit
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {

            TemplateButton(
                "NOTES",
                Modifier.weight(1f)
            ) {
                onSelected("Create a modern notes app with dark mode")
            }

            TemplateButton(
                "TODO",
                Modifier.weight(1f)
            ) {
                onSelected("Create a task management todo app")
            }

            TemplateButton(
                "CHAT",
                Modifier.weight(1f)
            ) {
                onSelected("Create a modern chat application")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {

            TemplateButton(
                "CALC",
                Modifier.weight(1f)
            ) {
                onSelected("Create a calculator app")
            }

            TemplateButton(
                "AI",
                Modifier.weight(1f)
            ) {
                onSelected("Create an AI assistant app")
            }

            TemplateButton(
                "CUSTOM",
                Modifier.weight(1f)
            ) {
                onSelected("")
            }
        }
    }
}

@Composable
fun TemplateButton(
    text: String,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    Box(
        modifier = modifier
            .graphicsLayer {
                translationY = if (pressed) 2.dp.toPx() else 0f
                shadowElevation = if (pressed) 2.dp.toPx() else 7.dp.toPx()
            }
            .background(GreenDark, RoundedCornerShape(7.dp))
            .border(
                width = if (pressed) 1.dp else 1.5.dp,
                color = if (pressed) Green else Border,
                shape = RoundedCornerShape(7.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick()
            }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Green,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun ProjectCard(
    name: String,
    description: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                translationY = if (pressed) 3.dp.toPx() else 0f
                shadowElevation = if (pressed) 3.dp.toPx() else 8.dp.toPx()
            }
            .background(Panel, RoundedCornerShape(8.dp))
            .border(
                width = if (pressed) 1.5.dp else 1.dp,
                color = if (pressed) Green else Border,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick()
            }
            .padding(13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "> $name",
                color = Green,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )

            Spacer(Modifier.height(3.dp))

            Text(
                text = description,
                color = Muted,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        Text(
            text = "›",
            color = Green,
            fontSize = 25.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun ProjectsScreen(
    projects: List<Project>,
    onProjectClick: (Project) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        SectionTitle("PROJECTS")

        if (projects.isEmpty()) {
            TerminalPanel {
                Text(
                    text = "> no projects found",
                    color = Muted,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp
                )

                Text(
                    text = "> create an app from the CREATE tab",
                    color = Muted,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp
                )
            }
        } else {
            projects.forEach { project ->
                ProjectCard(
                    name = project.name,
                    description = project.description,
                    onClick = {
                        onProjectClick(project)
                    }
                )
            }
        }
    }
}

@Composable
fun ProjectDetailScreen(
    project: Project,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onBuild: () -> Unit,
    onDelete: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "‹  PROJECT DETAIL",
            color = Green,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.clickable {
                onBack()
            }
        )

        TerminalPanel {

            Text(
                text = "> ${project.name}",
                color = Green,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = project.description,
                color = Muted,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        SectionTitle("PROJECT INFO")

        TerminalPanel {

            Text(
                text = "ID       : ${project.id}",
                color = TextMain,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )

            Text(
                text = "TEMPLATE : ${project.template}",
                color = TextMain,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )

            Text(
                text = "PACKAGE  : ${project.packageName}",
                color = TextMain,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )

            Text(
                text = "VERSION  : ${project.versionName}",
                color = TextMain,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        SectionTitle("AI PROMPT")

        TerminalPanel {
            SelectionContainer {
                Text(
                    text = project.prompt,
                    color = TextMain,
                    fontSize = 11.sp,
                    lineHeight = 17.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        SectionTitle("ACTIONS")

        GreenButton(
            text = "✎  EDIT PROJECT",
            onClick = onEdit
        )

        GreenButton(
            text = "▶  BUILD PROJECT",
            onClick = onBuild
        )

        GreenButton(
            text = "×  DELETE PROJECT",
            onClick = onDelete
        )

        Spacer(Modifier.height(10.dp))
    }
}

@Composable
fun SettingsScreen() {

    val context = LocalContext.current

    fun openUrl(url: String) {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(url)
            )
        )
    }

    fun openEmail() {
        context.startActivity(
            Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:gikoko611@gmail.com")
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        SectionTitle("SETTINGS")

        SettingsCard(
            "AI PROVIDER",
            "Groq • Gemini • OpenAI"
        )

        SettingsCard(
            "BUILD",
            "Cloud Build • APK • Release"
        )

        SettingsCard(
            "EDITOR",
            "Terminal Theme • Font Size"
        )

        SettingsCard(
            "SECURITY",
            "Secure API Key Storage"
        )

        SettingsCard(
            "ABOUT",
            "G.I APK Creator v0.4.0"
        )

        SectionTitle("CONTACT")

        ContactCard(
            title = "GITHUB",
            value = "github.com/gikoko611/GI-APK-Creator",
            onClick = {
                openUrl(
                    "https://github.com/gikoko611/GI-APK-Creator"
                )
            }
        )

        ContactCard(
            title = "EMAIL",
            value = "gikoko611@gmail.com",
            onClick = {
                openEmail()
            }
        )

        Spacer(Modifier.height(18.dp))

        Text(
            text = "G.I • BUILD SMART • BUILD CLOUD",
            color = Muted,
            fontFamily = FontFamily.Monospace,
            fontSize = 8.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun ContactCard(
    title: String,
    value: String,
    onClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Panel,
                RoundedCornerShape(11.dp)
            )
            .border(
                1.dp,
                Border,
                RoundedCornerShape(11.dp)
            )
            .clickable {
                onClick()
            }
            .padding(14.dp)
    ) {

        Text(
            text = "> $title",
            color = Green,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(5.dp))

        Text(
            text = value,
            color = TextMain,
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp
        )

        Spacer(Modifier.height(3.dp))

        Text(
            text = "TAP TO OPEN",
            color = Muted,
            fontFamily = FontFamily.Monospace,
            fontSize = 8.sp
        )
    }
}

@Composable
fun SettingsCard(
    title: String,
    description: String
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Panel,
                RoundedCornerShape(8.dp)
            )
            .border(
                1.dp,
                Border,
                RoundedCornerShape(8.dp)
            )
            .padding(14.dp)
    ) {

        Text(
            text = "> $title",
            color = Green,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = description,
            color = Muted,
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp
        )
    }
}

@Composable
fun BuildScreen(
    progress: Float,
    status: String,
    logs: List<String>,
    errorMessage: String,
    running: Boolean,
    onCancel: () -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "‹",
                color = Green,
                fontSize = 28.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.clickable {
                    onBack()
                }
            )

            Spacer(Modifier.width(8.dp))

            Column {

                Text(
                    text = "BUILD APK",
                    color = Green,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                Text(
                    text = "G.I CLOUD BUILD",
                    color = Muted,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        TerminalPanel {

            Text(
                text = "BUILD STATUS",
                color = Muted,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace
            )

            Spacer(Modifier.height(7.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = status,
                    color = when (status) {
                        "SUCCESS" -> Green
                        "FAILED" -> Color(0xFFFF5577)
                        "CANCELLED" -> Color(0xFFFFCC66)
                        else -> GreenSoft
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                Text(
                    text = "${(progress * 100).toInt()}%",
                    color = Green,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = Green,
                trackColor = GreenDark
            )
        }

        TerminalPanel {

            Text(
                text = "BUILD TERMINAL",
                color = Green,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )

            Spacer(Modifier.height(8.dp))

            SelectionContainer {

                Column {

                    logs.forEach { log ->

                        Text(
                            text = log,
                            color = if (
                                log.contains("completed") ||
                                log.contains("APK ready")
                            ) Green else Muted,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }

                    if (running) {

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = "> _",
                            color = Green,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            CopyTerminalButton(
                text = "COPY LOG",
                content = logs.joinToString("\n")
            )
        }

        if (status == "FAILED" && errorMessage.isNotBlank()) {

            BuildErrorPanel(
                errorMessage = errorMessage,
                logs = logs,
                onCopy = {
                    // Copy is handled inside BuildErrorPanel.
                }
            )
        }

        if (status == "SUCCESS") {

            TerminalPanel {

                Text(
                    text = "BUILD OUTPUT",
                    color = Green,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "✓ app-debug.apk",
                    color = TextMain,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )

                Text(
                    text = "Android Debug APK",
                    color = Muted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        if (running) {

            GreenButton(
                text = "■  CANCEL BUILD",
                onClick = onCancel
            )

        } else if (
            status == "SUCCESS" ||
            status == "FAILED" ||
            status == "CANCELLED"
        ) {

            GreenButton(
                text = "↻  BUILD AGAIN",
                onClick = onRetry
            )
        }

        Spacer(Modifier.height(20.dp))
    }
}

@Composable
fun BuildErrorPanel(
    errorMessage: String,
    logs: List<String>,
    onCopy: () -> Unit
) {

    val clipboard = LocalClipboardManager.current

    TerminalPanel {

        Text(
            text = "⚠ BUILD FAILED",
            color = Color(0xFFFF5577),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )

        Spacer(Modifier.height(8.dp))

        SelectionContainer {

            Text(
                text = errorMessage,
                color = Color(0xFFFF8899),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(Modifier.height(10.dp))

        Text(
            text = "ERROR LOG",
            color = Muted,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )

        Spacer(Modifier.height(5.dp))

        SelectionContainer {

            Column {

                logs.forEach { log ->

                    Text(
                        text = log,
                        color = Color(0xFFFF8899),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {
                clipboard.setText(
                    AnnotatedString(
                        buildString {
                            append(errorMessage)
                            append("\n\n")
                            append(logs.joinToString("\n"))
                        }
                    )
                )
                onCopy()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF35101A),
                contentColor = Color(0xFFFF8899)
            ),
            shape = RoundedCornerShape(7.dp)
        ) {

            Text(
                text = "COPY ERROR",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun CopyTerminalButton(
    text: String,
    content: String
) {

    val clipboard = LocalClipboardManager.current

    OutlinedButton(
        onClick = {
            clipboard.setText(AnnotatedString(content))
        },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Green
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            width = 1.dp
        ),
        shape = RoundedCornerShape(7.dp)
    ) {

        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun GreenButton(
    text: String,
    onClick: () -> Unit
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }

    val pressed by interactionSource.collectIsPressedAsState()

    Button(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .graphicsLayer {
                translationY = if (pressed) 3.dp.toPx() else 0f
                shadowElevation = if (pressed) 3.dp.toPx() else 10.dp.toPx()
            },
        colors = ButtonDefaults.buttonColors(
            containerColor = GreenDark,
            contentColor = Green
        ),
        shape = RoundedCornerShape(11.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp,
            pressedElevation = 2.dp
        )
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun GISplashScreen() {

    val transition = rememberInfiniteTransition(
        label = "gi_splash"
    )

    val rotation by transition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1600,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    val floatY by transition.animateFloat(
        initialValue = -7f,
        targetValue = 7f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1800,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Text(
                text = "G.I",
                color = Green,
                fontSize = 64.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier
                    .graphicsLayer {
                        rotationY = rotation
                        translationY = floatY
                        shadowElevation = 26.dp.toPx()
                    }
            )

            Text(
                text = "APK CREATOR",
                color = TextMain,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )

            Text(
                text = "> initializing G.I build system",
                color = Muted,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun TerminalPanel(
    content: @Composable ColumnScope.() -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Panel2,
                RoundedCornerShape(8.dp)
            )
            .border(
                1.dp,
                Border,
                RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        content = content
    )
}

@Composable
fun SectionTitle(
    text: String
) {

    Text(
        text = "// $text",
        color = Green,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace
    )
}

@Composable
fun DividerLine() {

    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Border)
    )
}

@Composable
fun BottomNavigation(
    selected: String,
    onSelected: (String) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Panel)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {

        BottomNavButton(
            text = "CREATE",
            selected = selected == "CREATE",
            modifier = Modifier.weight(1f)
        ) {
            onSelected("CREATE")
        }

        BottomNavButton(
            text = "PROJECTS",
            selected = selected == "PROJECTS",
            modifier = Modifier.weight(1f)
        ) {
            onSelected("PROJECTS")
        }

        BottomNavButton(
            text = "SETTINGS",
            selected = selected == "SETTINGS",
            modifier = Modifier.weight(1f)
        ) {
            onSelected("SETTINGS")
        }
    }
}

@Composable
fun BottomNavButton(
    text: String,
    selected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {

    Box(
        modifier = modifier
            .background(
                if (selected) GreenDark else Panel,
                RoundedCornerShape(7.dp)
            )
            .border(
                1.dp,
                if (selected) Green else Border,
                RoundedCornerShape(7.dp)
            )
            .clickable {
                onClick()
            }
            .padding(vertical = 11.dp),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = text,
            color = if (selected) Green else Muted,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}
