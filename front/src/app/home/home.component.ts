import { Component, OnInit } from '@angular/core';
import { TagModule } from "primeng/tag";
import { RippleModule } from "primeng/ripple";
import { CardModule } from "primeng/card";
import { TableModule } from "primeng/table";
import { ProgressBarModule } from "primeng/progressbar";
import { ChartModule } from "primeng/chart";
import { ButtonModule } from "primeng/button";
import { AvatarModule } from "primeng/avatar";
import { CommonModule, CurrencyPipe } from "@angular/common";
import { NavbarComponent } from "../_shared/components/navbar/navbar.component";

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    CardModule,
    TableModule,
    ChartModule,
    ProgressBarModule,
    ButtonModule,
    AvatarModule,
    RippleModule,
    TagModule,
    CurrencyPipe,
    NavbarComponent,
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  salesData = [
    { image: 'assets/product1.jpg', name: 'Bamboo Watch', price: 65 },
    { image: 'assets/product2.jpg', name: 'Black Watch', price: 72 },
    { image: 'assets/product3.jpg', name: 'Blue Band', price: 79 },
    { image: 'assets/product4.jpg', name: 'Blue T-Shirt', price: 29 },
    { image: 'assets/product5.jpg', name: 'Bracelet', price: 15 }
  ];
  chartData = {
    labels: ['Q1', 'Q2', 'Q3', 'Q4'],
    datasets: [
      {
        label: 'Subscriptions',
        data: [10000, 23000, 18000, 15000],
        backgroundColor: '#2ecc71'
      },
      {
        label: 'Advertising',
        data: [5000, 18000, 15000, 12000],
        backgroundColor: '#3498db'
      },
      {
        label: 'Affiliate',
        data: [3000, 12000, 10000, 8000],
        backgroundColor: '#e74c3c'
      }
    ]
  };

  bestSellingProducts = [
    { name: 'Space T-Shirt', category: 'Clothing', percentage: 50 },
    { name: 'Portal Sticker', category: 'Accessories', percentage: 16 }
  ];

  chartOptions: any;

  ngOnInit(): void {
    this.chartData = {
      labels: ['Q1', 'Q2', 'Q3', 'Q4'],
      datasets: [
        {
          label: 'Subscriptions',
          backgroundColor: '#34d399',
          data: [8000, 15000, 13000, 11000]
        },
        {
          label: 'Advertising',
          backgroundColor: '#6ee7b7',
          data: [2000, 4000, 3000, 3500]
        },
        {
          label: 'Affiliate',
          backgroundColor: '#a7f3d0',
          data: [1000, 3500, 2500, 3000]
        }
      ]
    };

    this.chartOptions = {
      plugins: {
        legend: {
          labels: {
            color: '#374151'
          }
        }
      },
      scales: {
        x: {
          ticks: {
            color: '#374151'
          },
          grid: {
            display: false
          }
        },
        y: {
          ticks: {
            color: '#374151'
          },
          grid: {
            color: '#e5e7eb'
          }
        }
      }
    };

  }
}
