package com.yes.tfgapp.data.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.yes.tfgapp.R
import com.yes.tfgapp.data.AppDataBase
import com.yes.tfgapp.data.repository.StockProductRepository
import com.yes.tfgapp.ui.home.MainActivity
import com.yes.tfgapp.ui.mystock.MyStockViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class MyWorker(context: Context, workerParameters: WorkerParameters) : CoroutineWorker(context, workerParameters) {

    private val stockProductRepository: StockProductRepository

    init {
        val stockProductDao = AppDataBase.getDatabase(context).stockProductDao()
        stockProductRepository = StockProductRepository(stockProductDao)
    }
    companion object {
        const val CHANNEL_ID = "channel_id"
        const val NOTIFICATION_ID = 1
    }

    override suspend fun doWork(): Result {
        updateExpireDate()
        scheduleNextWork()


        return Result.success()
    }


    private suspend fun updateExpireDate() {
        val allStockProducts = withContext(Dispatchers.IO) {
            stockProductRepository.getAllStockProducts()
        }

        allStockProducts?.forEach { product ->
            val expireDate = product.expirationDate
            val currentDate = Calendar.getInstance()
            val daysToExpire = daysBetweenDates(
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(currentDate.time),
                expireDate
            )
            Log.d(TAG, "DAYS TO EXPIRE: $daysToExpire")
            if(daysToExpire<=3){
                showNotification(product.name, daysToExpire.toInt())
            }
            val updatedProduct = product.copy(daysToExpire = daysToExpire.toInt())
            withContext(Dispatchers.IO) {
                stockProductRepository.updateStockProduct(updatedProduct)
            }
        }
    }


    private fun daysBetweenDates(startDate: String, endDate: String): Long {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val start = dateFormat.parse(startDate)
        val end = dateFormat.parse(endDate)
        if (start != null && end != null) {
            val diffInMillis = end.time - start.time
            return TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS)
        }
        return 0
    }

    private fun scheduleNextWork() {
        val currentDate = Calendar.getInstance()
        val nextRun = Calendar.getInstance()

        // Establecer la próxima ejecución a las 12 AM del siguiente día
        nextRun.set(Calendar.HOUR_OF_DAY, 0)
        nextRun.set(Calendar.MINUTE, 5)
        nextRun.set(Calendar.SECOND, 0)
        nextRun.set(Calendar.MILLISECOND, 0)
        nextRun.add(Calendar.DAY_OF_YEAR, 1)

        val initialDelay = nextRun.timeInMillis - currentDate.timeInMillis

        val workRequest = OneTimeWorkRequestBuilder<MyWorker>()
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(applicationContext).enqueue(workRequest)
    }

    private fun showNotification(name:String, daysToExpire: Int) {
        // Check if the notification permission is granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, handle the case accordingly
                return
            }
        }

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_check)
            .setContentTitle("Product $name will expire in $daysToExpire days")
            .setContentText("Check your stock")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Channel Name"
            val channelDescription = "Channel Description"
            val channelImportance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, channelName, channelImportance).apply {
                description = channelDescription
            }
            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        try {
            with(NotificationManagerCompat.from(applicationContext)) {
                notify(NOTIFICATION_ID, notification.build())
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
            // Handle the security exception, e.g., log it or notify the user
        }
    }
}
