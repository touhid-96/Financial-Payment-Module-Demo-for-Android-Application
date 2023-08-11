package com.example.recruitmenttask.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recruitmenttask.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class BkashPaymentActivity extends AppCompatActivity {

    private EditText number_ET, name_ET, amount_ET, narration_ET;
    private Button downloadReceipt, shareReceipt;
    private ImageView closeButton;
    private TextView sourceAccNo_TV, amount_TV, dateTime_TV, narration_TV, bkashNo_TV, name_TV, address_TV;
    private AlertDialog progressDialog;
    private Dialog reportDialog;
    FusedLocationProviderClient fusedLocationProviderClient;
    private final int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bkash_payment);

        initComponents();
    }

    private void initComponents() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(R.layout.layout_progress_dialog);
        progressDialog = dialogBuilder.create();

        reportDialog = new Dialog(this);
        reportDialog.setContentView(R.layout.layout_bkash_report_dialog);
        sourceAccNo_TV = (TextView) reportDialog.findViewById(R.id.src_acc_no_bkash);
        amount_TV = (TextView) reportDialog.findViewById(R.id.amount_bkash);
        dateTime_TV = (TextView) reportDialog.findViewById(R.id.date_time_bkash);
        narration_TV = (TextView) reportDialog.findViewById(R.id.narration_bkash);
        bkashNo_TV = (TextView) reportDialog.findViewById(R.id.bkash_no);
        name_TV = (TextView) reportDialog.findViewById(R.id.name_bkash);
        address_TV = (TextView) reportDialog.findViewById(R.id.address_bkash);
        downloadReceipt = (Button) reportDialog.findViewById(R.id.download_bkash);
        shareReceipt = (Button) reportDialog.findViewById(R.id.share_bkash);
        closeButton = (ImageView) reportDialog.findViewById(R.id.dialog_close_bkash);
        downloadReceipt.setOnClickListener(v -> {
            downloadReceiptDialog();
            //downloadReceiptDialog_2(); //using WRITE_EXTERNAL_STORAGE permission
        });
        shareReceipt.setOnClickListener(v -> {
            shareReceiptDialog();
        });
        closeButton.setOnClickListener(v -> {
            reportDialog.setCancelable(true);
            reportDialog.dismiss();
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        number_ET = (EditText) findViewById(R.id.bkash_number);
        name_ET = (EditText) findViewById(R.id.bkash_name);
        amount_ET = (EditText) findViewById(R.id.bkash_amount);
        narration_ET = (EditText) findViewById(R.id.bkash_narration);

        Button submitBtn = (Button) findViewById(R.id.submit_button);
        submitBtn.setOnClickListener(v -> {
            getCurrentLocation();
        });
    }

    private void downloadReceiptDialog() {
        PdfDocument pdfDocument = new PdfDocument();
        View view = reportDialog.getWindow().getDecorView().findViewById(R.id.payment_receipt);
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(view.getWidth(), view.getHeight(), 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        view.draw(canvas);
        pdfDocument.finishPage(page);

        //creating temp file in the apps cache directory
        String fileName = String.valueOf(new Random().nextInt(10000));
        File pdfFile = new File(getCacheDir(), "temp_receipt_"+ fileName +".pdf");
        try {
            OutputStream outputStream = new FileOutputStream(pdfFile);
            pdfDocument.writeTo(outputStream);
            outputStream.close();
        } catch (IOException ex) {
            makeToast(ex.getMessage());
        }

        pdfDocument.close();

        //download the pdf using an Intent with FileProvider
        Uri pdfUri = FileProvider.getUriForFile(this, "com.example.recruitmenttask", pdfFile);
        Intent downloadIntent = new Intent(Intent.ACTION_VIEW);
        downloadIntent.setDataAndType(pdfUri, "application/pdf");
        downloadIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(downloadIntent);
    }

    /**
     * method : downloadReceiptDialog_2()
     * This method requires WRITE_EXTERNAL_STORAGE permission.
     */
    private void downloadReceiptDialog_2() {
        PdfDocument pdfDocument = new PdfDocument();
        View view = reportDialog.getWindow().getDecorView().findViewById(R.id.payment_receipt);
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(view.getWidth(), view.getHeight(), 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        view.draw(canvas);
        pdfDocument.finishPage(page);

        //now filepath to write
        String folderPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        String fileName = String.valueOf(new Random().nextInt(10000));
        String filePath = folderPath + File.separator + "temp_receipt_" + fileName + ".pdf";
        File file = new File(filePath);
        try {
            OutputStream outputStream = new FileOutputStream(file);
            pdfDocument.writeTo(outputStream);
            outputStream.close();

            Toast.makeText(this, "Download Successful", Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            makeToast(ex.getMessage());
        }

        pdfDocument.close();
    }

    private void shareReceiptDialog() {
        PdfDocument pdfDocument = new PdfDocument();
        View view = reportDialog.getWindow().getDecorView().findViewById(R.id.payment_receipt);
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(view.getWidth(), view.getHeight(), 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        view.draw(canvas);
        pdfDocument.finishPage(page);

        //creating temp file in the apps cache directory
        String fileName = String.valueOf(new Random().nextInt(10000));
        File tempFile = new File(getCacheDir(), "temp_receipt_"+ fileName +".pdf");
        try {
            OutputStream outputStream = new FileOutputStream(tempFile);
            pdfDocument.writeTo(outputStream);
            outputStream.close();
        } catch (IOException ex) {
            makeToast(ex.getMessage());
        }

        pdfDocument.close();

        //share the pdf using an Intent with FileProvider
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/pdf");
        Uri pdfUri = FileProvider.getUriForFile(this, "com.example.recruitmenttask", tempFile);
        shareIntent.putExtra(Intent.EXTRA_STREAM, pdfUri);
        startActivity(shareIntent);
        //tempFile.delete();
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    Geocoder geocoder = new Geocoder(BkashPaymentActivity.this, Locale.getDefault());
                    List<Address> addresses = null;
                    try {
                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    } catch (IOException e) {
                        makeToast(e.getMessage());
                    }
                    String address = addresses.get(0).getAddressLine(0);
                    FormData(address);
                }
            });
        } else {
            askPermission();
        }
    }

    private void FormData(String Address) {
        String Number = number_ET.getText().toString();
        String Name = name_ET.getText().toString();
        String Amount = amount_ET.getText().toString();
        String Narration = narration_ET.getText().toString();
        String DateTime = getDateTime();

        if (Number.isEmpty()) {
            String message = "bKash Number Missing!";
            makeToast(message);
        } else if (Name.isEmpty()) {
            String message = "Name Missing!";
            makeToast(message);
        } else if (Amount.isEmpty()) {
            String message = "Amount Missing!";
            makeToast(message);
        } else if (Narration.isEmpty()) {
            String message = "Please write a narration!";
            makeToast(message);
        } else {
            generateReportDialog(Number, Name, Amount, Narration, DateTime, Address);
        }
    }

    private void generateReportDialog(String number, String name, String amount, String narration, String dateTime, String address) {
        /*String message = "Number = " + number + "\n" +
                         "Name = " + name + "\n" +
                         "Amount = " + amount + "\n" +
                         "Narration = " + narration + "\n" +
                         "Date-Time = " + dateTime + "\n" +
                         "Address = " + address;
        System.out.println(message);*/

        sourceAccNo_TV.setText(number);
        amount_TV.setText(amount);
        dateTime_TV.setText(dateTime);
        narration_TV.setText(narration);
        bkashNo_TV.setText(number);
        name_TV.setText(name);
        address_TV.setText(address);

        reportDialog.setCancelable(false);
        reportDialog.show();
    }

    private String getDateTime() {
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return dateTime.format(formatter);
    }

    private void makeToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showProgressDialog(Boolean yes) {
        if (yes) progressDialog.show();
        else progressDialog.dismiss();
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(BkashPaymentActivity.this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION
        }, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                String message = "Permission required!";
                makeToast(message);
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}