package com.nrikesari.app.ui.screens.projects

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.nrikesari.app.model.ProjectInquiry
import com.nrikesari.app.viewmodel.AuthViewModel
import com.nrikesari.app.viewmodel.UserViewModel

data class ServiceOption(
    val title: String,
    val description: String,
    val priceEstimate: String,
    val icon: ImageVector
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProjectEnquiryScreen(
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel
) {
    val currentUser = FirebaseAuth.getInstance().currentUser

    var currentStep by remember { mutableStateOf(1) }

    // Form inputs
    var selectedService by remember { mutableStateOf("Web Development") }
    
    // Client Info
    var fullName by remember {
        mutableStateOf(
            currentUser?.displayName ?: currentUser?.email?.substringBefore("@") ?: ""
        )
    }
    var email by remember { mutableStateOf(currentUser?.email ?: "") }
    var phone by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }

    // Project Info
    var projectType by remember { mutableStateOf("") }
    var budgetRange by remember { mutableStateOf("₹25k – ₹50k") }
    var timeline by remember { mutableStateOf("1 Month") }

    // Requirements
    var goals by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var fileUrl by remember { mutableStateOf("") }
    var additionalNotes by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf(false) }

    val serviceOptions = remember {
        listOf(
            ServiceOption("Web Development", "Premium responsive websites, SaaS, and web applications built using cutting edge stacks.", "₹5k – ₹80k", Icons.Default.Language),
            ServiceOption("App Development", "Native iOS & Android mobile apps with modern features, fluid animations, and smooth performance.", "₹40k – ₹3L", Icons.Default.Smartphone),
            ServiceOption("Graphic Design", "Stunning logo design, digital brand books, visual marketing collaterals, and identity packs.", "₹1k – ₹20k", Icons.Default.Palette),
            ServiceOption("Video Editing", "High-impact advertisement cuts, motion graphics, color grading, and cinematic reels.", "₹2k – ₹30k", Icons.Default.Movie),
            ServiceOption("3D / VFX", "Visual effects composition, high-end 3D environments, character animations, and simulations.", "₹20k – ₹70k", Icons.Default.ViewInAr),
            ServiceOption("Product Visualization", "Photo-realistic 3D packaging renders, industrial mockups, and commercial display graphics.", "₹5k – ₹40k", Icons.Default.Visibility),
            ServiceOption("Content Creation", "Creative copywriting, scripting, blog assets, and tailored social media campaign materials.", "₹3k – ₹40k", Icons.Default.Edit),
            ServiceOption("Digital Marketing", "Targeted search campaigns, programmatic advertising, organic SEO, and social growth strategies.", "₹8k – ₹1L", Icons.Default.Campaign)
        )
    }

    val scrollState = rememberScrollState()

    val background = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.02f)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(24.dp))

        /* HERO HEADER SECTION */
        Text(
            text = "Let's Build Something Extraordinary",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            lineHeight = 32.sp
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Tell us about your project and we will help you bring your vision to life.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )

        Spacer(Modifier.height(16.dp))

        /* VISUAL STEP PROGRESS TRACKER */
        ProgressTracker(currentStep = currentStep)

        Spacer(Modifier.height(16.dp))

        /* SUCCESS MESSAGE VIEW */
        if (successMessage) {
            Card(
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Inquiry Sent Successfully!",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Thank you! We will review your project details and get back to you within 24 hours.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
        } else {
            errorMessage?.let {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(Modifier.height(16.dp))
            }

            /* MULTI STEP LAYOUT */
            Crossfade(targetState = currentStep, label = "onboarding_steps") { step ->
                when (step) {
                    1 -> Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Step 1: Choose a Service",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        serviceOptions.forEach { option ->
                            ServiceCard(
                                service = option,
                                isSelected = selectedService == option.title,
                                onClick = { selectedService = option.title }
                            )
                        }
                    }
                    2 -> Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text(
                            text = "Step 2: Client Information",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Full Name") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email Address") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Phone Number") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = companyName,
                            onValueChange = { companyName = it },
                            label = { Text("Company Name (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    3 -> Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text(
                            text = "Step 3: Project Information",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        OutlinedTextField(
                            value = projectType,
                            onValueChange = { projectType = it },
                            label = { Text("Project Type (e.g. E-Commerce Website, SaaS app)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        
                        Text("Project Budget Range", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Below ₹25k", "₹25k – ₹50k", "₹50k – ₹1L", "₹1L – ₹3L", "₹3L+").forEach { range ->
                                FilterChip(
                                    selected = budgetRange == range,
                                    onClick = { budgetRange = range },
                                    label = { Text(range) }
                                )
                            }
                        }
                        
                        Text("Timeline Requirements", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Urgent (< 2 weeks)", "1 Month", "1-3 Months", "Flexible / Long-term").forEach { timeOpt ->
                                FilterChip(
                                    selected = timeline == timeOpt,
                                    onClick = { timeline = timeOpt },
                                    label = { Text(timeOpt) }
                                )
                            }
                        }
                    }
                    4 -> Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text(
                            text = "Step 4: Requirements",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        OutlinedTextField(
                            value = goals,
                            onValueChange = { goals = it },
                            label = { Text("What are the main goals of this project?") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Describe the project requirements in detail") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            shape = RoundedCornerShape(12.dp),
                            minLines = 3
                        )
                        OutlinedTextField(
                            value = fileUrl,
                            onValueChange = { fileUrl = it },
                            label = { Text("Attachment Link / Reference Assets URL (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = additionalNotes,
                            onValueChange = { additionalNotes = it },
                            label = { Text("Additional Notes (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    5 -> Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text(
                            text = "Step 5: Review & Submit",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text("Project Onboarding Summary", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.height(12.dp))
                                SummaryRow("Selected Service", selectedService)
                                SummaryRow("Project Type", projectType)
                                SummaryRow("Client Name", fullName)
                                SummaryRow("Email", email)
                                SummaryRow("Phone", phone)
                                if (companyName.isNotEmpty()) SummaryRow("Company", companyName)
                                SummaryRow("Budget Range", budgetRange)
                                SummaryRow("Timeline", timeline)
                                if (goals.isNotEmpty()) SummaryRow("Project Goals", goals)
                                if (fileUrl.isNotEmpty()) SummaryRow("Attachment URL", fileUrl)
                                
                                Spacer(Modifier.height(12.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                Spacer(Modifier.height(12.dp))
                                
                                Text("Detailed Description", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(Modifier.height(4.dp))
                                Text(description, style = MaterialTheme.typography.bodyMedium)
                                
                                if (additionalNotes.isNotEmpty()) {
                                    Spacer(Modifier.height(12.dp))
                                    Text("Additional Notes", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(Modifier.height(4.dp))
                                    Text(additionalNotes, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            /* STEP ACTIONS ROW */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (currentStep > 1) {
                    OutlinedButton(
                        onClick = { currentStep-- },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Icon(Icons.Default.ArrowBack, null)
                        Spacer(Modifier.width(6.dp))
                        Text("Back")
                    }
                    Spacer(Modifier.width(10.dp))
                }

                val actionText = if (currentStep == 5) "Submit Inquiry" else "Continue"
                val isSubmit = currentStep == 5
                
                Button(
                    onClick = {
                        if (isSubmit) {
                            val userId = currentUser?.uid
                            if (userId == null) {
                                errorMessage = "Please login first from Settings."
                                return@Button
                            }
                            val inquiry = ProjectInquiry(
                                userId = userId,
                                name = fullName.trim(),
                                contact = phone.trim().ifEmpty { email.trim() },
                                service = selectedService,
                                description = description.trim(),
                                email = email.trim(),
                                phone = phone.trim(),
                                companyName = companyName.trim(),
                                projectType = projectType.trim(),
                                budgetRange = budgetRange,
                                timeline = timeline,
                                goals = goals.trim(),
                                fileUrl = fileUrl.trim(),
                                additionalNotes = additionalNotes.trim(),
                                submittedAt = System.currentTimeMillis(),
                                status = "Pending"
                            )
                            userViewModel.submitProjectEnquiry(inquiry)
                            successMessage = true
                        } else {
                            when (currentStep) {
                                1 -> currentStep = 2
                                2 -> {
                                    if (fullName.isBlank()) {
                                        errorMessage = "Please enter your full name."
                                        return@Button
                                    }
                                    if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                        errorMessage = "Please enter a valid email address."
                                        return@Button
                                    }
                                    if (phone.isBlank()) {
                                        errorMessage = "Please enter your phone number."
                                        return@Button
                                    }
                                    errorMessage = null
                                    currentStep = 3
                                }
                                3 -> {
                                    if (projectType.isBlank()) {
                                        errorMessage = "Please specify project type."
                                        return@Button
                                    }
                                    errorMessage = null
                                    currentStep = 4
                                }
                                4 -> {
                                    if (description.isBlank()) {
                                        errorMessage = "Please enter project requirements description."
                                        return@Button
                                    }
                                    errorMessage = null
                                    currentStep = 5
                                }
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Text(actionText)
                    Spacer(Modifier.width(6.dp))
                    Icon(if (isSubmit) Icons.Default.Send else Icons.Default.ArrowForward, null)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        /* TRUST BUILDING PANEL */
        TrustBuildingPanel()
    }
}

@Composable
fun ProgressTracker(currentStep: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        (1..5).forEach { stepNum ->
            val isCompleted = stepNum < currentStep
            val isActive = stepNum == currentStep
            val circleColor = when {
                isActive -> MaterialTheme.colorScheme.primary
                isCompleted -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
            val textColor = when {
                isActive || isCompleted -> Color.White
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
            
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(circleColor),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(14.dp))
                } else {
                    Text(stepNum.toString(), color = textColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
            
            if (stepNum < 5) {
                Box(
                    modifier = Modifier
                        .width(32.dp)
                        .height(2.dp)
                        .background(
                            if (stepNum < currentStep) MaterialTheme.colorScheme.primary 
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                )
            }
        }
    }
}

@Composable
fun ServiceCard(
    service: ServiceOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp)
            ),
        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.04f) else MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary 
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = service.icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(service.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(service.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                Spacer(Modifier.height(4.dp))
                Text("Price estimate: ${service.priceEstimate}", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontSize = 13.sp, textAlign = TextAlign.End, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun TrustBuildingPanel() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 40.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Our Agency Promise",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        val trustItems = listOf(
            Pair(Icons.Default.FlashOn, "Fast Response" to "We respond within 24 hours with a detailed kickoff proposal."),
            Pair(Icons.Default.People, "Professional Team" to "Work with senior engineers, animators, and growth strategists directly."),
            Pair(Icons.Default.Visibility, "Transparent Process" to "Track development milestones in real-time with continuous updates."),
            Pair(Icons.Default.SupportAgent, "Dedicated Support" to "Enjoy post-delivery support and active SLA maintenance assistance."),
            Pair(Icons.Default.TaskAlt, "End-to-End Delivery" to "From creative concept scripts to code deployment, we handle it all.")
        )
        
        trustItems.forEach { (icon, texts) ->
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp).offset(y = 2.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(texts.first, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(texts.second, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                }
            }
        }
    }
}