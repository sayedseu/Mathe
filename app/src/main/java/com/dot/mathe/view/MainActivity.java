package com.dot.mathe.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.codekidlabs.storagechooser.StorageChooser;
import com.dot.mathe.R;
import com.dot.mathe.app.Injection;
import com.dot.mathe.app.ViewModelProviderFactory;
import com.dot.mathe.data.Resource;
import com.dot.mathe.databinding.ActivityMainBinding;
import com.dot.mathe.dialog.CommentDialog;
import com.dot.mathe.dialog.SavingDialog;
import com.dot.mathe.dialog.ShareDialog;
import com.dot.mathe.model.Comment;
import com.dot.mathe.model.RowData;
import com.dot.mathe.utils.CustomTextWatcher;
import com.dot.mathe.utils.Selection;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.snackbar.Snackbar;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.dot.mathe.utils.Spreadsheet.AVG;
import static com.dot.mathe.utils.Spreadsheet.COUNT;
import static com.dot.mathe.utils.Spreadsheet.MAX;
import static com.dot.mathe.utils.Spreadsheet.MEDIAN;
import static com.dot.mathe.utils.Spreadsheet.MIN;
import static com.dot.mathe.utils.Spreadsheet.MINUS;
import static com.dot.mathe.utils.Spreadsheet.SUM;
import static com.dot.mathe.utils.Spreadsheet.calculate;
import static com.dot.mathe.utils.Spreadsheet.getColumnIndex;
import static com.dot.mathe.utils.Spreadsheet.getExpressionValue;
import static com.dot.mathe.utils.Spreadsheet.isValidFormula;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener,
        View.OnFocusChangeListener,
        SavingDialog.SavingDialogListener,
        ShareDialog.ShareDialogListener,
        CustomTextWatcher.TextChangeListener,
        CommentDialog.CommentDialogListener {

    private final int MULTIPLE_PERMISSIONS = 111;
    private final String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private ActivityMainBinding binding;
    private EditText currentFocusedEditText;
    private MainActivityViewModel viewModel;
    private InterstitialAd interstitialAd;
    private Selection selection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initEditTextListener();
        ViewModelProviderFactory providerFactory = Injection.provideViewModelProviderFactory(this);
        viewModel = new ViewModelProvider(this, providerFactory).get(MainActivityViewModel.class);
        subscribeObserver();
        viewModel.retrieveAllRowData();

        MobileAds.initialize(this, getString(R.string.app_id));
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_id));
        interstitialAd.loadAd(new AdRequest.Builder().build());
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                afterAdClosed();
                interstitialAd.loadAd(new AdRequest.Builder().build());
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                afterAdClosed();
            }
        });
        showIndicator();
    }

    @Override
    protected void onStart() {
        super.onStart();
        binding.avg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentFocusedEditText != null) {
                    currentFocusedEditText.setText("=" + AVG + "()");
                    currentFocusedEditText.setSelection(currentFocusedEditText.getText().length() - 1);
                }
            }
        });

        binding.sum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentFocusedEditText != null) {
                    currentFocusedEditText.setText("=" + SUM + "()");
                    currentFocusedEditText.setSelection(currentFocusedEditText.getText().length() - 1);
                }
            }
        });

        binding.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentFocusedEditText != null) {
                    currentFocusedEditText.setText("=" + MINUS + "()");
                    currentFocusedEditText.setSelection(currentFocusedEditText.getText().length() - 1);
                }
            }
        });

        binding.max.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentFocusedEditText != null) {
                    currentFocusedEditText.setText("=" + MAX + "()");
                    currentFocusedEditText.setSelection(currentFocusedEditText.getText().length() - 1);
                }
            }
        });

        binding.min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentFocusedEditText != null) {
                    currentFocusedEditText.setText("=" + MIN + "()");
                    currentFocusedEditText.setSelection(currentFocusedEditText.getText().length() - 1);
                }
            }
        });

        binding.count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentFocusedEditText != null) {
                    currentFocusedEditText.setText("=" + COUNT + "()");
                    currentFocusedEditText.setSelection(currentFocusedEditText.getText().length() - 1);
                }
            }
        });

        binding.median.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentFocusedEditText != null) {
                    currentFocusedEditText.setText("=" + MEDIAN + "()");
                    currentFocusedEditText.setSelection(currentFocusedEditText.getText().length() - 1);
                }
            }
        });

        binding.equal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentFocusedEditText != null) {
                    String formula = currentFocusedEditText.getText().toString().trim();
                    boolean isValid = isValidFormula(formula);
                    if (isValid) {
                        int[] columnIndex = getColumnIndex(formula);
                        if (columnIndex != null) {
                            List<Double> sheetValue = getSheetValue(columnIndex[0], columnIndex[1]);
                            if (sheetValue.size() > 0) {
                                double result = calculate(formula, sheetValue);
                                currentFocusedEditText.setText(String.valueOf(result));
                                currentFocusedEditText.setSelection(currentFocusedEditText.getText().toString().length());
                            }
                        }
                    } else {
                        currentFocusedEditText.setBackgroundColor(getResources().getColor(R.color.colorRed));
                    }
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.export:
                selection = Selection.SAVE;
                showAd();
                return true;
            case R.id.share:
                selection = Selection.SHARE;
                showAd();
                return true;
            case R.id.clear:
                clearRow();
                viewModel.deleteAllComment();
                return true;
            case R.id.comment:
                if (currentFocusedEditText != null) {
                    viewModel.retrieveComment(currentFocusedEditText.getId());
                } else {
                    showSnackBar(R.string.select_row);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (view instanceof EditText) {
            view.setOnFocusChangeListener(this);
        }
        return false;
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if (b) {
            currentFocusedEditText = (EditText) view;
        }
    }

    private void initEditTextListener() {
        for (EditText editText : getEditTextList()) {
            editText.setOnTouchListener(this);
            editText.addTextChangedListener(new CustomTextWatcher(editText, this));
        }
    }

    private List<EditText> getEditTextList() {
        List<EditText> editTextList = new ArrayList<>();
        editTextList.add(binding.spreadsheet.column1a);
        editTextList.add(binding.spreadsheet.column1b);
        editTextList.add(binding.spreadsheet.column2a);
        editTextList.add(binding.spreadsheet.column2b);
        editTextList.add(binding.spreadsheet.column3a);
        editTextList.add(binding.spreadsheet.column3b);
        editTextList.add(binding.spreadsheet.column4a);
        editTextList.add(binding.spreadsheet.column4b);
        editTextList.add(binding.spreadsheet.column5a);
        editTextList.add(binding.spreadsheet.column5b);
        editTextList.add(binding.spreadsheet.column6a);
        editTextList.add(binding.spreadsheet.column6b);
        editTextList.add(binding.spreadsheet.column7a);
        editTextList.add(binding.spreadsheet.column7b);
        editTextList.add(binding.spreadsheet.column8a);
        editTextList.add(binding.spreadsheet.column8b);
        editTextList.add(binding.spreadsheet.column9a);
        editTextList.add(binding.spreadsheet.column9b);
        editTextList.add(binding.spreadsheet.column10a);
        editTextList.add(binding.spreadsheet.column10b);
        return editTextList;
    }

    private List<Double> getSheetValue(int start, int end) {
        List<Double> doubleList = new ArrayList<>();
        List<EditText> editTextList = getEditTextList();
        Pattern pattern = Pattern.compile("[+\\-]?(([0-9]+\\.[0-9]+)|([0-9]+\\.?)|(\\.?[0-9]+))([+\\-/*](([0-9]+\\.[0-9]+)|([0-9]+\\.?)|(\\.?[0-9]+)))*");
        if (start < end) {
            for (int i = start - 1; i <= end; i++) {
                EditText editText = editTextList.get(i);
                String value = editText.getText().toString().trim();
                if (!value.isEmpty()) {
                    if (pattern.matcher(value).matches()) {
                        doubleList.add(getExpressionValue(value));
                    }
                }
            }
        } else {
            for (int i = end - 1; i <= start - 1; i++) {
                EditText editText = editTextList.get(i);
                String value = editText.getText().toString().trim();
                if (!value.isEmpty()) {
                    if (pattern.matcher(value).matches()) {
                        doubleList.add(getExpressionValue(value));
                    }
                }
            }
        }
        return doubleList;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MULTIPLE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                chooseDirectories();
            }
        }
    }

    private void checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : permissions) {
            result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    MULTIPLE_PERMISSIONS);
        } else {
            chooseDirectories();
        }
    }

    private void chooseDirectories() {
        StorageChooser chooser = new StorageChooser.Builder()
                .withActivity(MainActivity.this)
                .withFragmentManager(getFragmentManager())
                .skipOverview(true)
                .allowCustomPath(true)
                .setType(StorageChooser.DIRECTORY_CHOOSER)
                .build();
        chooser.show();
        chooser.setOnSelectListener(new StorageChooser.OnSelectListener() {
            @Override
            public void onSelect(String path) {
                openSavingDialog(path);
            }
        });
    }

    private void openSavingDialog(String path) {
        SavingDialog savingDialog = new SavingDialog(path);
        savingDialog.show(getSupportFragmentManager(), "saving_dialog");
        savingDialog.setCancelable(false);
    }

    private void openShareDialog() {
        String path = this.getExternalFilesDir("").getAbsolutePath();
        ShareDialog shareDialog = new ShareDialog(path);
        shareDialog.show(getSupportFragmentManager(), "share_dialog");
        shareDialog.setCancelable(false);
    }

    private void createPdf(File file, boolean isShareable) {
        Document document = new Document();
        boolean isSuccess = true;
        try {
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();
        } catch (DocumentException | FileNotFoundException e) {
            isSuccess = false;
        }
        document.setPageSize(PageSize.A4);
        document.addCreationDate();
        document.addAuthor("TechnoVen");
        document.addCreator("TechnoVen");
        for (int i = 0; i <= 10; i++) {
            PdfPTable table = new PdfPTable(new float[]{1, 3, 3});
            try {
                switch (i) {
                    case 0:
                        table.addCell("");
                        table.addCell("A");
                        table.addCell("B");
                        document.add(table);
                        break;
                    case 1:
                        table.addCell("01");
                        table.addCell(getCellValue(0));
                        table.addCell(getCellValue(1));
                        document.add(table);
                        break;
                    case 2:
                        table.addCell("02");
                        table.addCell(getCellValue(2));
                        table.addCell(getCellValue(3));
                        document.add(table);
                        break;
                    case 3:
                        table.addCell("03");
                        table.addCell(getCellValue(4));
                        table.addCell(getCellValue(5));
                        document.add(table);
                        break;
                    case 4:
                        table.addCell("04");
                        table.addCell(getCellValue(6));
                        table.addCell(getCellValue(7));
                        document.add(table);
                        break;
                    case 5:
                        table.addCell("05");
                        table.addCell(getCellValue(8));
                        table.addCell(getCellValue(9));
                        document.add(table);
                        break;
                    case 6:
                        table.addCell("06");
                        table.addCell(getCellValue(10));
                        table.addCell(getCellValue(11));
                        document.add(table);
                        break;
                    case 7:
                        table.addCell("07");
                        table.addCell(getCellValue(12));
                        table.addCell(getCellValue(13));
                        document.add(table);
                        break;
                    case 8:
                        table.addCell("08");
                        table.addCell(getCellValue(14));
                        table.addCell(getCellValue(15));
                        document.add(table);
                        break;
                    case 9:
                        table.addCell("09");
                        table.addCell(getCellValue(16));
                        table.addCell(getCellValue(17));
                        document.add(table);
                        break;
                    case 10:
                        table.addCell("10");
                        table.addCell(getCellValue(18));
                        table.addCell(getCellValue(19));
                        document.add(table);
                        break;
                }
            } catch (DocumentException e) {
                isSuccess = false;
            }
        }
        document.close();
        if (isSuccess) {
            if (isShareable) {
                share(file);
            } else {
                showSnackBar("File saved to " + file.getAbsolutePath());
            }
        } else {
            showSnackBar(R.string.operation_failed);
        }
    }

    private void createXlsx(File file) {
        Workbook workbook = getWorkBook();
        try (FileOutputStream os = new FileOutputStream(file)) {
            workbook.write(os);
            showSnackBar("File saved to " + file.getAbsolutePath());
        } catch (Exception e) {
            showSnackBar(R.string.failed_to_save);
        }
    }

    private void shareXlsx(File file) {
        Workbook workbook = getWorkBook();
        try (FileOutputStream os = new FileOutputStream(file)) {
            workbook.write(os);
            share(file);
        } catch (Exception e) {
            showSnackBar(R.string.failed_to_share);
        }
    }

    private void share(File file) {
        Uri uri = FileProvider.getUriForFile(this, "com.dot.mathe", file);
        Intent intent = ShareCompat.IntentBuilder.from(this)
                .setType("application/*")
                .setStream(uri)
                .setChooserTitle("Choose bar")
                .createChooserIntent()
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(intent);
    }

    private Workbook getWorkBook() {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("Mathe");
        for (int i = 0; i < 10; i++) {
            Row row = sheet.createRow(i);
            switch (i) {
                case 0:
                    row.createCell(0).setCellValue(getCellValue(0));
                    row.createCell(1).setCellValue(getCellValue(1));
                    break;
                case 1:
                    row.createCell(0).setCellValue(getCellValue(2));
                    row.createCell(1).setCellValue(getCellValue(3));
                    break;
                case 2:
                    row.createCell(0).setCellValue(getCellValue(4));
                    row.createCell(1).setCellValue(getCellValue(5));
                    break;
                case 3:
                    row.createCell(0).setCellValue(getCellValue(6));
                    row.createCell(1).setCellValue(getCellValue(7));
                    break;
                case 4:
                    row.createCell(0).setCellValue(getCellValue(8));
                    row.createCell(1).setCellValue(getCellValue(9));
                    break;
                case 5:
                    row.createCell(0).setCellValue(getCellValue(10));
                    row.createCell(1).setCellValue(getCellValue(11));
                    break;
                case 6:
                    row.createCell(0).setCellValue(getCellValue(12));
                    row.createCell(1).setCellValue(getCellValue(13));
                    break;
                case 7:
                    row.createCell(0).setCellValue(getCellValue(14));
                    row.createCell(1).setCellValue(getCellValue(15));
                    break;
                case 8:
                    row.createCell(0).setCellValue(getCellValue(16));
                    row.createCell(1).setCellValue(getCellValue(17));
                    break;
                case 9:
                    row.createCell(0).setCellValue(getCellValue(18));
                    row.createCell(1).setCellValue(getCellValue(19));
                    break;
            }
        }
        return workbook;
    }

    private String getCellValue(int index) {
        List<EditText> editTextList = getEditTextList();
        if (editTextList.get(index).getText().toString().isEmpty()) {
            return "";
        }
        return editTextList.get(index).getText().toString();
    }

    @Override
    public void onShare(File file, boolean isXlsx) {
        if (isXlsx) {
            shareXlsx(file);
        } else {
            createPdf(file, true);
        }
    }

    @Override
    public void onSave(File file, boolean isXlsx) {
        if (isXlsx) {
            createXlsx(file);
        } else {
            createPdf(file, false);
        }
    }

    @Override
    public void onTextChange(int id, String text) {
        RowData rowData = new RowData(id, text);
        viewModel.insertRowData(rowData);
    }

    private void subscribeObserver() {
        viewModel.observeRowData().observe(this, new Observer<Resource<List<RowData>>>() {
            @Override
            public void onChanged(Resource<List<RowData>> resource) {
                if (resource != null) {
                    switch (resource.status) {
                        case LOADING:
                            binding.scrollView.setVisibility(View.INVISIBLE);
                            binding.progressBar.setVisibility(View.VISIBLE);
                            break;
                        case ERROR:
                            binding.progressBar.setVisibility(View.GONE);
                            binding.scrollView.setVisibility(View.VISIBLE);
                            break;
                        case SUCCESS:
                            binding.progressBar.setVisibility(View.GONE);
                            binding.scrollView.setVisibility(View.VISIBLE);
                            assert resource.data != null;
                            if (resource.data.size() > 0) {
                                updateUI(resource.data);
                            }
                    }
                }
            }
        });

        viewModel.observeComment().observe(this, new Observer<Resource<Comment>>() {
            @Override
            public void onChanged(Resource<Comment> resource) {
                if (resource != null) {
                    if (resource.status == Resource.Status.SUCCESS) {
                        showCommentDialog(resource.data);
                    }
                }
            }
        });

    }

    private void updateUI(List<RowData> rowData) {
        for (RowData data : rowData) {
            EditText editText = findViewById(data.id);
            editText.setText(data.data);
        }
    }

    private void clearRow() {
        for (EditText editText : getEditTextList()) {
            editText.setText("");
        }
    }

    private void showCommentDialog(Comment comment) {
        String title = "Writes comments for " + getColumnName(currentFocusedEditText.getId()) + ":";
        CommentDialog commentDialog = new CommentDialog(comment.text, title);
        commentDialog.show(getSupportFragmentManager(), "CommentDialog");
        commentDialog.setCancelable(false);
    }

    @Override
    public void onSaveComment(String text) {
        int id = currentFocusedEditText.getId();
        Comment comment = new Comment(id, text);
        viewModel.insertComment(comment);
    }

    private String getColumnName(int id) {
        switch (id) {
            case R.id.column_1a:
                return "A1";
            case R.id.column_1b:
                return "B1";

            case R.id.column_2a:
                return "A2";

            case R.id.column_2b:
                return "B2";

            case R.id.column_3a:
                return "A3";

            case R.id.column_3b:
                return "B3";

            case R.id.column_4a:
                return "A4";

            case R.id.column_4b:
                return "B4";

            case R.id.column_5a:
                return "A5";

            case R.id.column_5b:
                return "B5";

            case R.id.column_6a:
                return "A6";

            case R.id.column_6b:
                return "B6";

            case R.id.column_7a:
                return "A7";

            case R.id.column_7b:
                return "B7";

            case R.id.column_8a:
                return "A8";

            case R.id.column_8b:
                return "B8";

            case R.id.column_9a:
                return "A9";

            case R.id.column_9b:
                return "B9";

            case R.id.column_10a:
                return "A10";

            case R.id.column_10b:
                return "B10";
            default:
                return "";
        }
    }

    private void showSnackBar(int stringId) {
        Snackbar.make(binding.getRoot(), stringId, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                }).show();
    }

    private void showSnackBar(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                }).show();
    }

    private void showIndicator() {
        binding.horizontalScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                boolean right = binding.horizontalScrollView.canScrollHorizontally(-1);
                boolean left = binding.horizontalScrollView.canScrollHorizontally(1);
                if (right && !left) {
                    binding.directionRight.setVisibility(View.INVISIBLE);
                    binding.directionLeft.setVisibility(View.VISIBLE);
                } else if (left && !right) {
                    binding.directionLeft.setVisibility(View.INVISIBLE);
                    binding.directionRight.setVisibility(View.VISIBLE);
                }
            }
        });


    }

    private void showAd() {
        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
        } else {
            afterAdClosed();
        }
    }

    private void afterAdClosed() {
        switch (selection) {
            case SAVE:
                checkPermissions();
                break;
            case SHARE:
                openShareDialog();
                break;
        }
    }

}