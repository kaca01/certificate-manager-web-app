import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { CertificateService } from 'src/app/service/certificate.service';
import { CertificateRequestComponent } from '../certificate-request/certificate-request.component';

@Component({
  selector: 'certificate',
  templateUrl: './certificate.component.html',
  styleUrls: ['./certificate.component.css']
})
export class CertificateComponent implements OnInit {
  displayedColumns: string[] = ['serial number', 'subject', 'valid from', 'valid to', 'type', 'download'];
  dataSource!: MatTableDataSource<Certificate>;
  certificate: Certificate[] = [];
  condition: boolean = true;

  @ViewChild(MatPaginator) paginator!: any;
  @ViewChild(MatSort) sort!: any;

  constructor(private certificateService: CertificateService, private dialog: MatDialog) {}

  ngOnInit(): void {
    this.certificate = this.certificateService.getAll();
    this.dataSource = new MatTableDataSource<Certificate>(this.certificate);
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  openDialog() {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;
    
    this.dialog.open(CertificateRequestComponent, dialogConfig);
  }
}

export interface Certificate {
  _id: number;
  serialNumber: string;
  subject: string;
  validFrom: string;
  validTo: string;
  type: string;
}

export interface AllCertificate {
  totalCount: number;
  results: Certificate[];
}
