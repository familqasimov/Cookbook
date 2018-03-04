package com.github.jnuutinen.cookbook.presentation.about;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.github.jnuutinen.cookbook.R;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.text_version_number) TextView versionnumber;
    @BindView(R.id.text_about_url) TextView gitHubLink;

    private AlertDialog licenseDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        buildLicenseDialog();
        getVersionNumber();
    }

    @OnClick(R.id.button_license)
    void showLicense() {
        licenseDialog.show();
    }

    @OnClick(R.id.button_oss_licenses)
    void showOssLicenses() {
        startActivity(new Intent(this, OssLicensesMenuActivity.class));
    }

    private void buildLicenseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.action_license)
                .setMessage("Copyright 2018 Juha Nuutinen\n" +
                        "\n" +
                        "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                        "you may not use this file except in compliance with the License.\n" +
                        "You may obtain a copy of the License at\n" +
                        "\n" +
                        "http://www.apache.org/licenses/LICENSE-2.0\n" +
                        "\n" +
                        "Unless required by applicable law or agreed to in writing, software\n" +
                        "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                        "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                        "See the License for the specific language governing permissions and\n" +
                        "limitations under the License.");
        licenseDialog = builder.create();
    }

    private void getVersionNumber() {
        try {
            String version = getApplicationContext().getPackageManager()
                    .getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;
            versionnumber.setText(getString(R.string.title_version_number, version));
        } catch (PackageManager.NameNotFoundException e) {
            versionnumber.setText("");
        }
    }

}
