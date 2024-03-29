import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { CertificateService } from 'src/app/service/certificate.service';
import { CertificateRequestService } from './certificate-request.service';
import { UserService } from 'src/app/service/user.service';
import { AllCertificate, CertificateRequest, User } from 'src/app/domains';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { ReCaptchaV3Service } from 'ng-recaptcha';


@Component({
  selector: 'app-certificate-request',
  templateUrl: './certificate-request.component.html',
  styleUrls: ['./certificate-request.component.css']
})
export class CertificateRequestComponent implements OnInit {
  issuers: AllCertificate = {} as AllCertificate;
  issuer: string = "";
  type: string = "";

  constructor(private router: Router, private dialogRef: MatDialogRef<CertificateRequestComponent>, private certificateService: CertificateService,
              private certificateRequsetService: CertificateRequestService, private userService: UserService, private snackBar: MatSnackBar,
              private recaptchaV3Service: ReCaptchaV3Service, @Inject(MAT_DIALOG_DATA) data: any) {

     }

  ngOnInit(): void {
    if (this.userService.currentUser == undefined || this.userService.currentUser == null)
      this.router.navigate(['/welcome-page']);
    this.certificateService.getIssuers().subscribe((res)=> {
      this.issuers = res;
    }, (error) => {
      this.openSnackBar("We had some problems with finding issuers. Please try again later.", 5000);
    });

  }

  save(): void {
    if ((this.type != "ROOT" && this.issuer == "") || (this.type == "")) {
      this.openSnackBar("Required fields are empty!");
      return;
    }
    let certifcateRequest: CertificateRequest = {} as CertificateRequest;
    certifcateRequest.certificateType = this.type;
    certifcateRequest.issuer = this.issuer;
    certifcateRequest.requestType = "ACTIVE";
    certifcateRequest.subject = {} as User;
    console.log(certifcateRequest);
    if (this.userService.currentUser?.id != undefined) certifcateRequest.subject.id = this.userService.currentUser?.id;

    this.recaptchaV3Service.execute('importantAction')
    .subscribe((token: string) => {
      console.log(`Token generated`);
      this.certificateRequsetService.insert(certifcateRequest, token).subscribe((res)=> {
        this.openSnackBar("Successfully added!");
      }, (error) => {
        console.log(error);
        this.openSnackBar(error.error.message, 5000);
      });
    });

    this.dialogRef.close();
  }

  close(): void{
    this.dialogRef.close();
  }

  openSnackBar(snackMsg : string, duration: number = 2000) : void {
    this.snackBar.open(snackMsg, "Dismiss", {
      duration: duration
    });
  }

}
