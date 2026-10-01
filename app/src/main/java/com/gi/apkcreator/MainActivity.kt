package com.gi.apkcreator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

    var prompt by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("CREATE") }
    var status by remember { mutableStateOf("ready_") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
    ) {

        Header()

        when (selectedTab) {
            "CREATE" -> CreateScreen(
                prompt = prompt,
                onPromptChange = { prompt = it },
                onStatusChange = { status = it }
            )

            "PROJECTS" -> ProjectsScreen()

            "SETTINGS" -> SettingsScreen()
        }

        Spacer(Modifier.weight(1f))

        BottomNavigation(
            selected = selectedTab,
            onSelected = { selectedTab = it }
        )
    }
}

@Composable
fun Header() {
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
                fontFamily = FontFamily.Monospace
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
                fontFamily = FontFamily.Monospace
            )
        }
    }

    DividerLine()
}

@Composable
fun CreateScreen(
    prompt: String,
    onPromptChange: (String) -> Unit,
    onStatusChange: (String) -> Unit
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
            onClick = {
                onStatusChange("analyzing request...")
            }
        )

        SectionTitle("QUICK TEMPLATES")

        TemplateGrid(
            onSelected = onPromptChange
        )

        SectionTitle("YOUR PROJECTS")

        ProjectCard(
            name = "My Notes",
            description = "Android • Compose",
            onClick = {
                onStatusChange("opening My Notes...")
            }
        )

        ProjectCard(
            name = "AI Assistant",
            description = "Android • Compose",
            onClick = {
                onStatusChange("opening AI Assistant...")
            }
        )

        ProjectCard(
            name = "Todo Manager",
            description = "Android • Compose",
            onClick = {
                onStatusChange("opening Todo Manager...")
            }
        )

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

    Box(
        modifier = modifier
            .background(GreenDark, RoundedCornerShape(7.dp))
            .border(
                1.dp,
                Border,
                RoundedCornerShape(7.dp)
            )
            .clickable { onClick() }
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Panel, RoundedCornerShape(8.dp))
            .border(
                1.dp,
                Border,
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
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
fun ProjectsScreen() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        SectionTitle("PROJECTS")

        ProjectCard(
            "My Notes",
            "Android • Compose",
            {}
        )

        ProjectCard(
            "AI Assistant",
            "Android • Compose",
            {}
        )

        ProjectCard(
            "Todo Manager",
            "Android • Compose",
            {}
        )
    }
}

@Composable
fun SettingsScreen() {

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
            "G.I APK Creator v0.3.0"
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
            .background(Panel, RoundedCornerShape(8.dp))
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
fun BottomNavigation(
    selected: String,
    onSelected: (String) -> Unit
) {

    Column {

        DividerLine()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Panel)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            NavItem(
                icon = "⌂",
                label = "CREATE",
                selected = selected == "CREATE"
            ) {
                onSelected("CREATE")
            }

            NavItem(
                icon = "▣",
                label = "PROJECTS",
                selected = selected == "PROJECTS"
            ) {
                onSelected("PROJECTS")
            }

            NavItem(
                icon = "⚙",
                label = "SETTINGS",
                selected = selected == "SETTINGS"
            ) {
                onSelected("SETTINGS")
            }
        }
    }
}

@Composable
fun NavItem(
    icon: String,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 3.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = icon,
            color = if (selected) Green else Muted,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace
        )

        Text(
            text = label,
            color = if (selected) Green else Muted,
            fontSize = 8.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun GreenButton(
    text: String,
    onClick: () -> Unit
) {

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = GreenDark,
            contentColor = Green
        ),
        shape = RoundedCornerShape(8.dp)
    ) {

        Text(
            text = text,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}

@Composable
fun TerminalPanel(
    content: @Composable ColumnScope.() -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Panel, RoundedCornerShape(8.dp))
            .border(
                1.dp,
                Border,
                RoundedCornerShape(8.dp)
            )
            .padding(13.dp),
        content = content
    )
}

@Composable
fun SectionTitle(
    title: String
) {

    Text(
        text = "// $title",
        color = GreenSoft,
        fontFamily = FontFamily.Monospace,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun DividerLine() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Border)
    )
}
