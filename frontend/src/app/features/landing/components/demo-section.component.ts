import { Component } from '@angular/core';


@Component({
  selector: 'app-demo-section',
  standalone: true,
  imports: [],
  templateUrl: './demo-section.component.html',
})
export class DemoSectionComponent {
  readonly dashboards = [
    {
      title: 'Gym Owner Dashboard',
      caption: 'Monitor memberships, revenue, trainers, and daily class operations from one place.',
      audience: 'Business Owners',
      image:
        'https://images.unsplash.com/photo-1593079831268-3381b0db4a77?auto=format&fit=crop&w=900&q=80',
    },
    {
      title: 'Trainer Dashboard',
      caption: 'Assign workout plans, track attendance, and guide members through measurable progress.',
      audience: 'Coaching Teams',
      image:
        'https://images.unsplash.com/photo-1518611012118-696072aa579a?auto=format&fit=crop&w=900&q=80',
    },
    {
      title: 'Member Dashboard',
      caption: 'Book classes, follow routines, and track workouts, body stats, and membership status.',
      audience: 'Gym Members',
      image:
        'https://images.pexels.com/photos/3823039/pexels-photo-3823039.jpeg?auto=compress&cs=tinysrgb&w=900',
    },
  ];
}
