import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from '../infrastructure/app-routing.module';
import { AppComponent } from './app.component';
import { WelcomePageComponent } from './components/welcome-page/welcome-page.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialModule } from 'src/infrastructure/material.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { LoginComponent } from './components/login/login.component';
import { CertificateComponent } from './components/certificate/certificate.component';
import { CertificateRequestComponent } from './components/certificate-request/certificate-request.component';
import { AuthService } from './service/auth.service';

import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { TokenInterceptor } from './components/interceptor/tokenInterceptor';
import { ApiService } from './service/api.service';
import { UserService } from './service/user.service';
import { ConfigService } from './service/config.service';
import { CertificateService } from './service/certificate.service';
import { NavigationComponent } from './components/navigation/navigation.component';
import { RegistrationComponent } from './components/registration/registration.component';
import { RequestsComponent } from './components/requests/requests.component';
import { WithdrawalReasonComponent } from './components/certificate/withdrawal-reason/withdrawal-reason.component';
import { ActivationComponent } from './components/registration/activation/activation/activation.component';
import { ResetPasswordComponent } from './components/reset-password/reset-password.component';
import { FileUploadDialogComponent } from './components/certificate/file-upload-dialog/file-upload-dialog.component';
import { AddReasonDialogComponent } from './components/requests/add-reason-dialog/add-reason-dialog.component';
import { HistoryComponent } from './components/history/history.component';
import { RECAPTCHA_V3_SITE_KEY, RecaptchaV3Module } from 'ng-recaptcha';
import { recaptcha } from 'src/environments/environment.prod';

@NgModule({
  declarations: [
    AppComponent,
    WelcomePageComponent,
    LoginComponent,
    CertificateComponent,
    CertificateRequestComponent,
    NavigationComponent,
    RegistrationComponent,
    RequestsComponent,
    WithdrawalReasonComponent,
    ActivationComponent,
    ResetPasswordComponent,
    FileUploadDialogComponent,
    AddReasonDialogComponent,
    HistoryComponent,
  ],
  imports: [
    FormsModule,
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MaterialModule,
    ReactiveFormsModule,
    HttpClientModule,
    RecaptchaV3Module,
  ],
  providers: [
  {
    provide: HTTP_INTERCEPTORS,
    useClass: TokenInterceptor,
    multi: true,
  },
  {
    provide: RECAPTCHA_V3_SITE_KEY,
    useValue: recaptcha.siteKey,
  },
    AuthService,
    ApiService,
    UserService,
    ConfigService,
    CertificateService
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}

