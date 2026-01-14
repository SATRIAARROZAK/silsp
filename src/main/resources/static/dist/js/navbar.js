// document.addEventListener("DOMContentLoaded", function () {
//   fetchNotifications();

//   // Opsional: Polling setiap 30 detik agar realtime
//   setInterval(fetchNotifications, 5000);
// });

// function fetchNotifications() {
//   fetch("/api/notifications/unread")
//     .then((response) => response.json())
//     .then((data) => {
//       const countBadge = document.getElementById("notifCount");
//       const header = document.getElementById("notifHeader");
//       const listContainer = document.getElementById("notifList");

//       // 1. Update Badge Lonceng
//       if (data.count > 0) {
//         countBadge.innerText = data.count;
//         countBadge.style.display = "inline-block";
//         header.innerText = data.count + " Notifikasi Baru";
//       } else {
//         countBadge.style.display = "none";
//         header.innerText = "Tidak ada notifikasi baru";
//       }

//       // 2. Update List Dropdown
//       if (data.data && data.data.length > 0) {
//         let html = "";
//         data.data.forEach((n) => {
//           // Format Waktu Sederhana
//           const date = new Date(n.time);
//           const timeStr = date.toLocaleTimeString([], {
//             hour: "2-digit",
//             minute: "2-digit",
//           });

//           // Style background jika belum dibaca
//           const bgStyle = n.isRead ? "" : "background-color: #f8f9fa;";

//           html += `
//                         <a href="${n.url}" class="dropdown-item" style="${bgStyle}">
//                             <div class="media">
//                                 <div class="media-body">
//                                     <h3 class="dropdown-item-title font-weight-bold text-sm">
//                                         ${n.title}
//                                     </h3>
//                                     <p class="text-sm text-muted text-wrap mb-1">${n.message}</p>
//                                     <p class="text-sm text-muted"><i class="far fa-clock mr-1"></i> ${timeStr}</p>
//                                 </div>
//                             </div>
//                         </a>
//                         <div class="dropdown-divider"></div>
//                         `;
//         });
//         listContainer.innerHTML = html;
//       } else {
//         listContainer.innerHTML =
//           '<span class="dropdown-item dropdown-footer text-muted">Belum ada notifikasi</span>';
//       }
//     })
//     .catch((err) => console.error("Gagal memuat notifikasi:", err));
// }


document.addEventListener("DOMContentLoaded", function() {
        fetchNotifications();
        // Polling tiap 5 detik
        setInterval(fetchNotifications, 30000); 
    });

    function fetchNotifications() {
        fetch('/api/notifications/unread')
            .then(response => response.json())
            .then(data => {
                const countBadge = document.getElementById('notifCount');
                const header = document.getElementById('notifHeader');
                const listContainer = document.getElementById('notifList');

                // 1. Update Badge
                if (data.count > 0) {
                    countBadge.innerText = data.count;
                    countBadge.style.display = 'inline-block';
                    header.innerText = data.count + " Notifikasi Baru";
                } else {
                    countBadge.style.display = 'none';
                    header.innerText = "Tidak ada notifikasi baru";
                }

                // 2. Render List
                if (data.data && data.data.length > 0) {
                    let html = '';
                    data.data.forEach(n => {
                        // Logic Warna: Jika isRead=false -> 'notif-unread' (Biru Muda), else -> Putih
                        const itemClass = n.isRead ? '' : 'notif-unread';
                        
                        // Hitung waktu relatif (contoh sederhana)
                        const date = new Date(n.time);
                        const timeStr = date.toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'});

                        html += `
                        <a href="${n.url}" class="dropdown-item ${itemClass}" onclick="markOneAsRead(${n.id})">
                            <div class="media">
                                <div class="media-body">
                                    <h3 class="dropdown-item-title font-weight-bold text-sm text-wrap">
                                        ${n.title}
                                    </h3>
                                    <p class="text-sm text-muted text-wrap mb-1">${n.message}</p>
                                    <p class="text-xs text-muted mb-0"><i class="far fa-clock mr-1"></i> ${timeStr}</p>
                                </div>
                            </div>
                        </a>
                        <div class="dropdown-divider"></div>
                        `;
                    });
                    listContainer.innerHTML = html;
                } else {
                    listContainer.innerHTML = '<span class="dropdown-item dropdown-footer text-muted py-3">Belum ada notifikasi</span>';
                }
            })
            .catch(err => console.error('Gagal fetch notif:', err));
    }

    // --- FUNGSI TANDAI SEMUA DIBACA ---
    function markAllAsRead() {
        fetch('/api/notifications/mark-read', { method: 'POST' })
            .then(() => {
                // Refresh UI langsung tanpa reload
                fetchNotifications();
                
                // Opsional: Toast notifikasi
                const Toast = Swal.mixin({
                  toast: true, position: 'top-end', showConfirmButton: false, timer: 2000
                });
                Toast.fire({ icon: 'success', title: 'Semua notifikasi ditandai dibaca' });
            });
    }

    // --- FUNGSI TANDAI SATU (Saat item diklik) ---
    function markOneAsRead(id) {
        // Kirim request di background, browser tetap lanjut navigasi ke URL href
        navigator.sendBeacon('/api/notifications/mark-read/' + id);
    }