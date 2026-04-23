// ChartService – wraps Chart.js to create/update canvas charts. Injectable singleton; components call createXxxChart helpers.
import { Injectable } from '@angular/core';
import {
  Chart,
  ChartConfiguration,
  registerables,
} from 'chart.js';

Chart.register(...registerables);

@Injectable({ providedIn: 'root' })
export class ChartService {

  // Destroy an existing chart on a canvas before re-creating it.
  destroy(chart: Chart | undefined): void {
    chart?.destroy();
  }

  // Create a line chart for time-series data (e.g. monthly revenue).
  createLineChart(
    canvas: HTMLCanvasElement,
    labels: string[],
    datasets: { label: string; data: number[]; color: string }[],
  ): Chart {
    const config: ChartConfiguration<'line'> = {
      type: 'line',
      data: {
        labels,
        datasets: datasets.map((d) => ({
          label: d.label,
          data: d.data,
          borderColor: d.color,
          backgroundColor: d.color + '22',
          tension: 0.4,
          fill: true,
          pointRadius: 4,
          pointHoverRadius: 6,
        })),
      },
      options: this.baseOptions('left'),
    };
    return new Chart(canvas, config);
  }

  // Create a doughnut/pie chart for distribution data (e.g. role counts).
  createDoughnutChart(
    canvas: HTMLCanvasElement,
    labels: string[],
    data: number[],
    colors: string[],
  ): Chart {
    const config: ChartConfiguration<'doughnut'> = {
      type: 'doughnut',
      data: { labels, datasets: [{ data, backgroundColor: colors, borderWidth: 2, borderColor: '#1a1a2e' }] },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { position: 'right', labels: { color: '#94a3b8', padding: 16, font: { size: 13 } } },
        },
      },
    };
    return new Chart(canvas, config);
  }

  // Create a bar chart for comparative data (e.g. bookings per class).
  createBarChart(
    canvas: HTMLCanvasElement,
    labels: string[],
    datasets: { label: string; data: number[]; color: string }[],
  ): Chart {
    const config: ChartConfiguration<'bar'> = {
      type: 'bar',
      data: {
        labels,
        datasets: datasets.map((d) => ({
          label: d.label, data: d.data,
          backgroundColor: d.color + 'bb',
          borderColor: d.color,
          borderWidth: 1, borderRadius: 4,
        })),
      },
      options: this.baseOptions('left'),
    };
    return new Chart(canvas, config);
  }

  private baseOptions(yAxis: 'left' | 'right') {
    return {
      responsive: true,
      maintainAspectRatio: false,
      interaction: { mode: 'index' as const, intersect: false },
      plugins: {
        legend: { labels: { color: '#94a3b8', font: { size: 13 } } },
      },
      scales: {
        x: { ticks: { color: '#64748b' }, grid: { color: '#1e293b' } },
        y: { position: yAxis, ticks: { color: '#64748b' }, grid: { color: '#1e293b' } },
      },
    };
  }
}
