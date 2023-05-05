import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { ConfigService } from './config.service';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { ResetPassword, User } from '../domains';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  public currentUser : User | null = null;

  constructor(
    private apiService: ApiService,
    private config: ConfigService,
    private http: HttpClient) { }

  getMyInfo() {
    return this.apiService.get(this.config.current_user_url)
      .pipe(map(user => {
        this.currentUser = user;
        return user;
    }));
  }

  login(user:any) : Observable<any>{
    return this.http.post<any>(environment.apiHost + "api/user/login", user);
  }

  sendEmail(userEmail: string): Observable<any> {
    return this.http.get<any>(environment.apiHost + 'api/user/' + userEmail + "/resetPassword");
  }

  resetPassword(userEmail: string, resetPassword: ResetPassword): Observable<void> {
    return this.http.put<void>(environment.apiHost + 'api/user/' + userEmail + "/resetPassword", resetPassword);
  }

}
