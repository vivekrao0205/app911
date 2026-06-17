package com.nrikesari.app.ui.screens.admin

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nrikesari.app.firebase.FirebaseService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAnalyticsScreen(navController: NavController) {
    val context = LocalContext.current
    val firebaseService = remember { FirebaseService() }
    val coroutineScope = rememberCoroutineScope()

    var activeFilter by remember { mutableStateOf("Monthly") } // Daily, Weekly, Monthly, Yearly
    var selectedMetric by remember { mutableStateOf("Users") } // Users, Projects, Messages, Bookings

    var stats by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val statsResult = firebaseService.getAnalyticsCounts()
        if (statsResult.isSuccess) {
            stats = statsResult.getOrDefault(emptyMap())
        }
        isLoading = false
    }

    // Custom mock points for charting growth based on activeFilter
    val chartDataPoints = remember(selectedMetric, activeFilter) {
        when (activeFilter) {
            "Daily" -> listOf(10f, 15f, 12f, 18f, 22f, 25f, 30f)
            "Weekly" -> listOf(45f, 60f, 55f, 75f, 80f, 95f, 120f)
            "Monthly" -> listOf(150f, 180f, 220f, 210f, 270f, 310f, 380f)
            "Yearly" -> listOf(500f, 850f, 1200f, 1900f, 2800f, 3900f, 5000f)
            else -> listOf(10f, 30f, 20f, 50f, 40f, 80f, 90f)
        }
    }

    val chartLabels = remember(activeFilter) {
        when (activeFilter) {
            "Daily" -> listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            "Weekly" -> listOf("W1", "W2", "W3", "W4", "W5", "W6", "W7")
            "Monthly" -> listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul")
            "Yearly" -> listOf("2020", "2021", "2022", "2023", "2024", "2025", "2026")
            else -> listOf("P1", "P2", "P3", "P4", "P5", "P6", "P7")
        }
    }

    fun exportToCSV() {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val filename = "Agency_Analytics_${selectedMetric}_${activeFilter}.csv"
                val downloadsFolder = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadsFolder, filename)
                val fos = FileOutputStream(file)
                
                // Write headers & data
                val builder = StringBuilder()
                builder.append("Label,Value\n")
                chartLabels.forEachIndexed { idx, label ->
                    builder.append("$label,${chartDataPoints[idx]}\n")
                }
                
                fos.write(builder.toString().toByteArray())
                fos.close()

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "CSV exported: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun exportToPDF() {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val pdfDocument = PdfDocument()
                val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
                val page = pdfDocument.startPage(pageInfo)
                val canvas = page.canvas
                val paint = Paint()

                // Title
                paint.textSize = 24f
                paint.isFakeBoldText = true
                paint.color = android.graphics.Color.BLACK
                canvas.drawText("Agency Analytics Summary", 50f, 80f, paint)

                // Subtitle
                paint.textSize = 14f
                paint.isFakeBoldText = false
                paint.color = android.graphics.Color.GRAY
                canvas.drawText("Metric: $selectedMetric | Range: $activeFilter", 50f, 110f, paint)

                // Statistics counts
                paint.textSize = 12f
                paint.color = android.graphics.Color.BLACK
                var yOffset = 180f
                canvas.drawText("Current Totals:", 50f, 150f, paint)
                
                stats.forEach { (key, valInt) ->
                    canvas.drawText("- $key: $valInt", 70f, yOffset, paint)
                    yOffset += 24f
                }

                // Chart Growth Data Table
                yOffset += 20f
                canvas.drawText("Growth Tracking Timeline:", 50f, yOffset, paint)
                yOffset += 30f

                paint.isFakeBoldText = true
                canvas.drawText("Timeline Interval", 50f, yOffset, paint)
                canvas.drawText("Growth Counter", 300f, yOffset, paint)
                yOffset += 16f
                paint.isFakeBoldText = false

                chartLabels.forEachIndexed { index, label ->
                    canvas.drawText(label, 50f, yOffset, paint)
                    canvas.drawText(chartDataPoints[index].toString(), 300f, yOffset, paint)
                    yOffset += 20f
                }

                pdfDocument.finishPage(page)

                val filename = "Agency_Analytics_${selectedMetric}_${activeFilter}.pdf"
                val downloadsFolder = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadsFolder, filename)
                pdfDocument.writeTo(FileOutputStream(file))
                pdfDocument.close()

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "PDF exported: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    AdminDrawerLayout(
        navController = navController,
        currentRoute = "admin_analytics",
        title = "Analytics & Reports"
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(MaterialTheme.colorScheme.background)
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(Modifier.height(10.dp))

                /* METRIC CHIP TOGGLES */
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Users", "Projects", "Messages", "Bookings").forEach { metric ->
                        val isSel = selectedMetric == metric
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { selectedMetric = metric }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(metric, color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                /* RANGE FILTER DROPDOWN */
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Time Range", fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Daily", "Weekly", "Monthly", "Yearly").forEach { filter ->
                            val isSel = activeFilter == filter
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent)
                                    .border(0.5.dp, if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(6.dp))
                                    .clickable { activeFilter = filter }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(filter, color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }

                /* CANVAS CHART DISPLAY */
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("$selectedMetric Growth Timeline ($activeFilter)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(20.dp))
                        
                        val strokeColor = MaterialTheme.colorScheme.primary
                        val gridColor = MaterialTheme.colorScheme.outlineVariant
                        
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        ) {
                            val maxVal = chartDataPoints.maxOrNull() ?: 100f
                            val width = size.width
                            val height = size.height
                            val spacing = width / (chartDataPoints.size - 1)

                            // Draw horizontal grid lines
                            val gridLines = 4
                            for (i in 0..gridLines) {
                                val y = height - (height / gridLines) * i
                                drawLine(
                                    color = gridColor,
                                    start = Offset(0f, y),
                                    end = Offset(width, y),
                                    strokeWidth = 1f
                                )
                            }

                            // Draw growth line graph
                            val pathPoints = chartDataPoints.mapIndexed { idx, point ->
                                val x = spacing * idx
                                val y = height - (point / maxVal) * height * 0.85f - (height * 0.05f)
                                Offset(x, y)
                            }

                            for (i in 0 until pathPoints.size - 1) {
                                drawLine(
                                    color = strokeColor,
                                    start = pathPoints[i],
                                    end = pathPoints[i + 1],
                                    strokeWidth = 4f
                                )
                                drawCircle(
                                    color = strokeColor,
                                    radius = 6f,
                                    center = pathPoints[i]
                                )
                            }
                            drawCircle(
                                color = strokeColor,
                                radius = 6f,
                                center = pathPoints.last()
                            )
                        }

                        // Labels Row
                        Spacer(Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            chartLabels.forEach { label ->
                                Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                /* EXPORT BUTTONS */
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Export Analytics Summary", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { exportToCSV() },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Download, null)
                                Spacer(Modifier.width(6.dp))
                                Text("Export CSV")
                            }
                            OutlinedButton(
                                onClick = { exportToPDF() },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Download, null)
                                Spacer(Modifier.width(6.dp))
                                Text("Export PDF")
                            }
                        }
                    }
                }

                Spacer(Modifier.height(40.dp))
            }
        }
    }
}
