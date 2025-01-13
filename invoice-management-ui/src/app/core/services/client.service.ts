import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { Client } from "../models/client";

@Injectable({
    providedIn: 'root'
  })
  export class ClientService {
    private apiUrl = 'http://localhost:8081/clients';
  
    constructor(private http: HttpClient) {}
  
    getClients(): Observable<Client[]> {
      return this.http.get<Client[]>(this.apiUrl);
    }
  
    deleteClient(id: number): Observable<void> {
      return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }
  }