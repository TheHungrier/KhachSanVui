function changeMainImage(src) {
    const mainImg = document.getElementById('mainImage');
    if (mainImg) mainImg.src = src;
}

function openGalleryModal() {
    const modal = document.getElementById('galleryModal');
    if (modal) modal.style.display = 'flex';
}

function closeGalleryModal() {
    const modal = document.getElementById('galleryModal');
    if (modal) modal.style.display = 'none';
}

document.addEventListener('DOMContentLoaded', function () {

    function startCountdowns() {
        const countdownBoxes = document.querySelectorAll('.countdown-box');
        if (!countdownBoxes.length) return;

        countdownBoxes.forEach(box => {
            const endTimeStr = box.getAttribute('data-endtime');
            if (!endTimeStr) return;
            let parts = endTimeStr.split(/[ /:]/);
            if (parts.length < 6) return;
            let endDate = new Date(parts[0], parts[1] - 1, parts[2], parts[3], parts[4], parts[5]);
            let endTime = endDate.getTime();
            if (isNaN(endTime)) return;

            const updateTimer = () => {
                const now = Date.now();
                const diff = endTime - now;
                if (diff <= 0) {
                    box.innerHTML = '<div><strong>00</strong><span>Giờ</span></div><b>:</b><div><strong>00</strong><span>Phút</span></div><b>:</b><div><strong>00</strong><span>Giây</span></div>';
                    return;
                }
                const hours = Math.floor(diff / (1000 * 60 * 60));
                const minutes = Math.floor((diff % (3600000)) / (1000 * 60));
                const seconds = Math.floor((diff % (60000)) / 1000);
                box.innerHTML = `
                    <div><strong>${hours.toString().padStart(2, '0')}</strong><span>Giờ</span></div>
                    <b>:</b>
                    <div><strong>${minutes.toString().padStart(2, '0')}</strong><span>Phút</span></div>
                    <b>:</b>
                    <div><strong>${seconds.toString().padStart(2, '0')}</strong><span>Giây</span></div>
                `;
            };
            updateTimer();
            setInterval(updateTimer, 1000);
        });
    }

    function initCarousels() {
        const carouselButtons = document.querySelectorAll('[data-carousel-target]');
        carouselButtons.forEach(button => {
            if (button.hasListener) return;
            button.hasListener = true;
            button.addEventListener('click', function (e) {
                const targetId = this.getAttribute('data-carousel-target');
                const direction = parseInt(this.getAttribute('data-carousel-direction') || '1');
                const carousel = document.getElementById(targetId);
                if (!carousel) return;
                const firstCard = carousel.querySelector('a, .ksv-room-card, .visual-card');
                const gap = 20;
                const scrollAmount = firstCard ? firstCard.offsetWidth + gap : 340;
                carousel.scrollBy({
                    left: direction * scrollAmount,
                    behavior: 'smooth'
                });
            });
        });
    }

    function initHeaderScroll() {
        const header = document.getElementById('siteHeader');
        if (!header) return;
        const toggleHeader = () => {
            if (window.scrollY > 24) {
                header.classList.add('scrolled');
            } else {
                header.classList.remove('scrolled');
            }
        };
        window.addEventListener('scroll', toggleHeader, {passive: true});
        toggleHeader();
    }

    function updateFlashSalesRealtime() {
        const cards = document.querySelectorAll('.flash-deal-card');
        if (cards.length === 0) return;

        fetch('/api/khuyen-mai/active-flash-sales')
            .then(response => {
                if (!response.ok) throw new Error('Network response was not ok');
                return response.json();
            })
            .then(data => {
                if (cards.length !== data.length) {
                    window.location.reload();
                    return;
                }
                data.forEach((km, index) => {
                    const card = cards[index];
                    const remainingSpan = card.querySelector('.deal-progress-label span');
                    if (remainingSpan) {
                        remainingSpan.innerText = km.soLuongPhongGioiHan - km.soLuongDaDat;
                    }
                    const progressSpan = card.querySelector('.deal-progress span');
                    if (progressSpan && km.soLuongPhongGioiHan > 0) {
                        const percent = (km.soLuongDaDat * 100) / km.soLuongPhongGioiHan;
                        progressSpan.style.width = percent + '%';
                    }
                });
            })
            .catch(err => console.warn('Không thể cập nhật flash sale:', err));
    }

    function initStarRating() {
        const starInputs = document.querySelectorAll('.star-rating input');
        starInputs.forEach(radio => {
            radio.addEventListener('change', function () {
                const value = parseInt(this.value);
                const starsContainer = this.closest('.star-rating');
                if (!starsContainer) return;
                const stars = starsContainer.querySelectorAll('label i');
                stars.forEach((star, index) => {
                    if (index < value) {
                        star.classList.remove('fa-regular');
                        star.classList.add('fa-solid');
                    } else {
                        star.classList.remove('fa-solid');
                        star.classList.add('fa-regular');
                    }
                });
            });
        });
    }

    function initDetailTabsScroll() {
        document.querySelectorAll('.ksv-room-detail-tabs a').forEach(anchor => {
            anchor.addEventListener('click', function (e) {
                e.preventDefault();
                const targetId = this.getAttribute('href');
                const targetElement = document.querySelector(targetId);

                if (targetElement) {
                    const headerOffset = document.getElementById('siteHeader')?.offsetHeight || 80;
                    const elementPosition = targetElement.getBoundingClientRect().top;
                    const offsetPosition = elementPosition + window.pageYOffset - headerOffset - 15;

                    window.scrollTo({
                        top: offsetPosition,
                        behavior: 'smooth'
                    });

                    document.querySelectorAll('.ksv-room-detail-tabs a').forEach(tab => tab.classList.remove('active'));
                    this.classList.add('active');
                }
            });
        });
    }

    function initBookingSidebarSync() {
        const inputNgayNhan = document.getElementById('ngayNhan');
        const inputNgayTra = document.getElementById('ngayTra');
        const inputSoKhach = document.getElementById('soLuongKhach');

        const txtNgayNhan = document.getElementById('txtViewNgayNhan');
        const txtNgayTra = document.getElementById('txtViewNgayTra');
        const txtSoKhach = document.getElementById('txtViewSoKhach');
        const txtSoDem = document.getElementById('txtHienThiSoDem');
        const inputSoDemHidden = document.getElementById('inputSoDem');
        const txtTongTien = document.getElementById('txtTongTien');

        window.soTienGiamGia = window.soTienGiamGia || 0;

        function formatAndDisplayDate(inputElement, textElement) {
            if (inputElement && inputElement.value && textElement) {
                const dateParts = inputElement.value.split('-');
                if (dateParts.length === 3) {
                    textElement.innerText = `${dateParts[2]}/${dateParts[1]}/${dateParts[0]}`;
                }
            } else if (textElement) {
                textElement.innerText = "Chọn ngày";
            }
        }

        window.capNhatSoDem = function () {
            if (inputNgayNhan && inputNgayTra && inputNgayNhan.value && inputNgayTra.value) {
                const nhan = new Date(inputNgayNhan.value);
                const tra = new Date(inputNgayTra.value);
                const diffTime = tra - nhan;
                const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

                let soDem = diffDays > 0 ? diffDays : 1;

                if (txtSoDem) txtSoDem.innerText = soDem + " đêm";
                if (inputSoDemHidden) inputSoDemHidden.value = soDem;

                const giaGoc = window.GIA_PHONG_GOC || 0;
                if (giaGoc > 0 && txtTongTien) {
                    const tienPhongGoc = giaGoc * soDem;
                    const thuePhi = tienPhongGoc * 0.1;
                    const tongTienTruocGiam = tienPhongGoc + thuePhi;
                    const tongTienCuoi = Math.max(0, tongTienTruocGiam - window.soTienGiamGia);

                    const lblTienPhong = document.getElementById('lblTienPhongHienThi');
                    const txtTienPhong = document.getElementById('txtTienPhongThucTe');
                    const txtThue = document.getElementById('txtThuePhiAside');

                    if (lblTienPhong) lblTienPhong.innerText = `Tiền phòng (${soDem} đêm)`;
                    if (txtTienPhong) txtTienPhong.innerText = "VND " + new Intl.NumberFormat('vi-VN').format(tienPhongGoc);
                    if (txtThue) txtThue.innerText = "VND " + new Intl.NumberFormat('vi-VN').format(thuePhi);

                    txtTongTien.innerText = "VND " + new Intl.NumberFormat('vi-VN').format(tongTienCuoi);
                }
            }
        }

        if (inputNgayNhan) {
            inputNgayNhan.addEventListener('change', () => {
                formatAndDisplayDate(inputNgayNhan, txtNgayNhan);
                window.capNhatSoDem();
            });
        }
        if (inputNgayTra) {
            inputNgayTra.addEventListener('change', () => {
                formatAndDisplayDate(inputNgayTra, txtNgayTra);
                window.capNhatSoDem();
            });
        }
        if (inputSoKhach) {
            inputSoKhach.addEventListener('input', () => {
                if (txtSoKhach) txtSoKhach.innerText = inputSoKhach.value || "1";
            });
        }

        window.capNhatSoDem();
    }

    function initPromoCodeHandler() {
        const btn = document.getElementById('btnApDung');
        const input = document.getElementById('inputMaGiamGia');
        if (!btn || !input) return;

        btn.addEventListener('click', function () {
            if (!input.checkValidity()) {
                input.reportValidity();
                return;
            }

            if (btn.dataset.applied === 'true') {
                alert('Mã giảm giá này đã được áp dụng thành công trước đó!');
                return;
            }

            const maCode = input.value.trim();
            let soDem = Number(document.getElementById('inputSoDem')?.value) || 1;
            const giaPhongGoc = window.GIA_PHONG_GOC || 0;
            const tongTienTamTinh = (giaPhongGoc * soDem) * 1.1;

            fetch(`/api/khuyen-mai/check?code=${maCode}&tongTien=${tongTienTamTinh}`)
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        window.soTienGiamGia = data.soTienGiam;

                        const boxGiamGia = document.getElementById('boxGiamGia');
                        const txtTienGiamGia = document.getElementById('txtTienGiamGia');
                        if (boxGiamGia) boxGiamGia.style.display = 'flex';
                        if (txtTienGiamGia) txtTienGiamGia.innerText = "-VND " + Number(data.soTienGiam).toLocaleString('vi-VN');

                        window.capNhatSoDem();

                        btn.dataset.applied = 'true';
                        btn.disabled = true;
                        input.readOnly = true;

                        alert(data.message || `Áp dụng thành công!`);
                    } else {
                        window.soTienGiamGia = 0;
                        btn.removeAttribute('data-applied');
                        alert(data.message || "Mã giảm giá không hợp lệ hoặc đã hết lượt sử dụng!");
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert("⚠️ Không thể kết nối đến máy chủ để kiểm tra mã giảm giá lúc này!");
                });
        });
    }

    function initFormValidation() {
        const form = document.querySelector('.ksv-checkout-form');
        if (!form) return;

        function showError(id, msg) {
            const el = document.getElementById('err-' + id);
            if (!el) return;
            el.innerText = msg || '';
            el.style.display = msg ? 'block' : 'none';
        }

        function validateField(id) {
            const f = form.querySelector('[name="' + id + '"]') || document.getElementById(id);
            const v = f ? String(f.value).trim() : '';
            const today = new Date();
            today.setHours(0, 0, 0, 0);

            switch (id) {
                case 'ngayNhan':
                    if (!v) return 'Vui lòng chọn ngày nhận phòng.';
                    if (new Date(v) < today) return 'Ngày nhận phải từ hôm nay trở đi.';
                    return '';
                case 'ngayTra':
                    if (!v) return 'Vui lòng chọn ngày trả phòng.';
                    const inputNhan = document.getElementById('ngayNhan') || form.querySelector('[name="ngayNhan"]');
                    const nv = inputNhan ? inputNhan.value : '';
                    if (nv && new Date(v) <= new Date(nv)) return 'Ngày trả phải sau ngày nhận.';
                    return '';
                case 'soLuongKhach':
                    if (!v) return 'Vui lòng nhập số khách.';
                    const n = Number(v);
                    const maxInput = document.getElementById('soLuongKhach')?.getAttribute('max');
                    if (!Number.isInteger(n) || n < 1) return 'Số khách phải là số nguyên >= 1.';
                    if (maxInput && n > Number(maxInput)) return `Số khách vượt quá sức chứa tối đa của phòng (${maxInput} người).`;
                    return '';
                case 'hoTen':
                    if (!v) return 'Vui lòng nhập họ tên.';
                    return '';
                case 'soDienThoai':
                    if (!v) return 'Vui lòng nhập số điện thoại.';
                    const sdtDigits = v.replace(/\D/g, '');
                    if (sdtDigits.length !== 10) return 'Số điện thoại phải là 10 chữ số.';
                    return '';
                case 'cccd':
                    if (!v) return '';
                    const cccdDigits = v.replace(/\D/g, '');
                    if (cccdDigits.length !== 12) return 'CCCD phải là 12 chữ số.';
                    return '';
                default:
                    return '';
            }
        }

        const fields = ['ngayNhan', 'ngayTra', 'soLuongKhach', 'hoTen', 'soDienThoai', 'cccd'];
        fields.forEach(id => {
            const f = document.getElementById(id) || form.querySelector('[name="' + id + '"]');
            if (!f) return;
            f.addEventListener('blur', function () {
                const err = validateField(id);
                showError(id, err);
            });
            f.addEventListener('input', function () {
                showError(id, '');
            });
        });

        form.addEventListener('submit', function (e) {
            let first = null;
            fields.forEach(id => {
                const err = validateField(id);
                showError(id, err);
                if (err && !first) first = id;
            });
            if (first) {
                e.preventDefault();
                const ref = document.getElementById(first) || form.querySelector('[name="' + first + '"]');
                if (ref) ref.focus();
                return false;
            }
            return true;
        });
    }

    function initAboutSlider() {
        const slider = document.getElementById('ksvAboutSlider');
        if (!slider) return;

        const slides = Array.from(slider.querySelectorAll('img'));
        const dotsContainer = document.getElementById('ksvAboutDots');

        if (!dotsContainer || slides.length === 0) return;

        let current = 0;
        let timer = null;

        if (!dotsContainer.hasChildNodes()) {
            slides.forEach(function (_, index) {
                const dot = document.createElement('button');
                dot.className = 'ksv-about-dot' + (index === 0 ? ' ksv-dot-active' : '');
                dot.setAttribute('aria-label', 'Xem ảnh ' + (index + 1));
                dot.setAttribute('role', 'tab');
                dot.setAttribute('aria-selected', index === 0 ? 'true' : 'false');

                dot.addEventListener('click', function () {
                    goTo(index);
                    resetTimer();
                });

                dotsContainer.appendChild(dot);
            });
        }

        const dots = Array.from(dotsContainer.querySelectorAll('.ksv-about-dot'));

        function goTo(n) {
            slides[current].classList.remove('ksv-slide-active');
            dots[current].classList.remove('ksv-dot-active');
            dots[current].setAttribute('aria-selected', 'false');

            current = (n + slides.length) % slides.length;

            slides[current].classList.add('ksv-slide-active');
            dots[current].classList.add('ksv-dot-active');
            dots[current].setAttribute('aria-selected', 'true');
        }

        function next() {
            goTo(current + 1);
        }

        function resetTimer() {
            clearInterval(timer);
            timer = setInterval(next, 4000);
        }

        document.addEventListener('visibilitychange', function () {
            if (document.hidden) {
                clearInterval(timer);
            } else {
                resetTimer();
            }
        });

        let touchStartX = 0;
        slider.addEventListener('touchstart', function (e) {
            touchStartX = e.changedTouches[0].clientX;
        }, {passive: true});

        slider.addEventListener('touchend', function (e) {
            const diff = touchStartX - e.changedTouches[0].clientX;
            if (Math.abs(diff) > 50) {
                goTo(diff > 0 ? current + 1 : current - 1);
                resetTimer();
            }
        }, {passive: true});

        resetTimer();
    }

    function initServiceDetail() {
        const form = document.getElementById('ksvServiceForm');
        if (!form) return;

        const qtyInput = document.getElementById('soLuong');
        const totalEl = document.getElementById('ksvTotalDisplay');
        const btnMinus = document.getElementById('ksvQtyMinus');
        const btnPlus = document.getElementById('ksvQtyPlus');
        const unitPrice = Number(qtyInput?.dataset.unitPrice || 0);

        function formatVND(amount) {
            return amount.toLocaleString('vi-VN') + ' ₫';
        }

        function syncTotal() {
            let qty = parseInt(qtyInput.value, 10);
            if (isNaN(qty) || qty < 1) qty = 1;
            if (qty > 99) qty = 99;
            qtyInput.value = qty;
            if (totalEl) totalEl.textContent = formatVND(unitPrice * qty);
        }

        if (btnMinus) btnMinus.addEventListener('click', function () {
            const q = parseInt(qtyInput.value, 10) || 1;
            if (q > 1) {
                qtyInput.value = q - 1;
                syncTotal();
            }
        });

        if (btnPlus) btnPlus.addEventListener('click', function () {
            const q = parseInt(qtyInput.value, 10) || 1;
            qtyInput.value = Math.min(99, q + 1);
            syncTotal();
        });

        if (qtyInput) {
            qtyInput.addEventListener('input', syncTotal);
            qtyInput.addEventListener('change', syncTotal);
        }

        form.addEventListener('submit', function () {
            syncTotal();
        });

        syncTotal();
    }

    function initServiceSidebarCart() {
        const form = document.getElementById('ksvSidebarForm');
        if (!form) return;

        const cards = document.querySelectorAll('.ksv-js-selectable');
        const itemsList = document.getElementById('sidebarCartItems');
        const totalDisplay = document.getElementById('ksvSidebarTotalDisplay');
        const submitBtn = document.getElementById('btnSubmitCart');

        let cart = [];

        function renderSidebar() {
            if (cart.length === 0) {
                itemsList.innerHTML = `
                <div class="ksv-cart-empty-state" style="padding: 30px 10px; text-align: center; color: #999;">
                    <i class="fa-solid fa-basket-shopping" style="font-size: 32px; margin-bottom: 10px; color: #ccc;"></i>
                    <p style="font-size: 13px; margin: 0;">Vui lòng chọn dịch vụ ở bên trái để thêm vào chuyến đi</p>
                </div>`;
                totalDisplay.textContent = '0 ₫';
                submitBtn.disabled = true;
                cards.forEach(c => c.classList.remove('active'));

                if (typeof window.capNhatSoDem === 'function') window.capNhatSoDem();
                return;
            }

            itemsList.innerHTML = '';
            let total = 0;

            cart.forEach((item, index) => {
                const itemPrice = parseFloat(item.price);
                const subtotal = itemPrice * item.qty;
                total += subtotal;

                const itemEl = document.createElement('div');
                itemEl.className = 'ksv-sidebar-item';

                itemEl.style.display = 'flex';
                itemEl.style.justifyContent = 'space-between';
                itemEl.style.alignItems = 'center';
                itemEl.style.backgroundColor = '#f8f9fa';
                itemEl.style.padding = '10px 12px';
                itemEl.style.borderRadius = '8px';
                itemEl.style.marginBottom = '10px';
                itemEl.style.border = '1px solid #eef0f2';
                itemEl.style.transition = 'all 0.2s ease';

                itemEl.innerHTML = `
                <div class="ksv-sidebar-item-info" style="flex: 1; padding-right: 8px;">
                    <div class="ksv-sidebar-item-name" style="font-weight: 600; font-size: 13.5px; color: #2d3748; line-height: 1.4; margin-bottom: 2px;">${item.name}</div>
                    <div class="ksv-sidebar-item-price" style="font-size: 12.5px; color: #ff5a5f; font-weight: 500;">${itemPrice.toLocaleString('vi-VN')}đ</div>
                </div>
                <div class="ksv-sidebar-item-actions" style="display: flex; align-items: center; gap: 10px;">
                    <div class="ksv-qty-controls" style="display: flex; align-items: center; background: #ffffff; border: 1px solid #cbd5e0; border-radius: 6px; overflow: hidden; height: 28px;">
                        <button type="button" class="ksv-qty-btn btn-minus" data-index="${index}" style="border:none; background:none; width: 26px; height: 100%; cursor:pointer; font-size: 14px; color: #4a5568; display: flex; align-items: center; justify-content: center; transition: background 0.2s;">-</button>
                        <span class="ksv-qty-value" style="min-width: 24px; text-align:center; font-size: 13px; font-weight: 600; color: #2d3748;">${item.qty}</span>
                        <button type="button" class="ksv-qty-btn btn-plus" data-index="${index}" style="border:none; background:none; width: 26px; height: 100%; cursor:pointer; font-size: 14px; color: #4a5568; display: flex; align-items: center; justify-content: center; transition: background 0.2s;">+</button>
                    </div>
                    <button type="button" class="ksv-remove-item-btn" data-index="${index}" style="background: #fff0f1; border: none; color: #ff4d4f; width: 28px; height: 28px; border-radius: 6px; cursor: pointer; display: flex; align-items: center; justify-content: center; transition: all 0.2s;" title="Xóa dịch vụ">
                        <i class="fa-solid fa-trash-can" style="font-size: 12px;"></i>
                    </button>
                </div>
            `;
                itemsList.appendChild(itemEl);
            });

            totalDisplay.textContent = total.toLocaleString('vi-VN') + ' ₫';
            submitBtn.disabled = false;

            cards.forEach(card => {
                const id = card.getAttribute('data-id');
                const isInCart = cart.some(item => item.id === id);
                if (isInCart) {
                    if (!card.classList.contains('active')) card.classList.add('active');
                } else {
                    card.classList.remove('active');
                }
            });

            if (typeof window.capNhatSoDem === 'function') {
                window.capNhatSoDem();
            }

            addEventListeners();
        }

        function addEventListeners() {
            itemsList.querySelectorAll('.ksv-qty-btn').forEach(btn => {
                btn.addEventListener('mouseover', function () {
                    this.style.backgroundColor = '#edf2f7';
                });
                btn.addEventListener('mouseout', function () {
                    this.style.backgroundColor = 'transparent';
                });
            });
            itemsList.querySelectorAll('.ksv-remove-item-btn').forEach(btn => {
                btn.addEventListener('mouseover', function () {
                    this.style.backgroundColor = '#ffccc7';
                });
                btn.addEventListener('mouseout', function () {
                    this.style.backgroundColor = '#fff0f1';
                });
            });

            itemsList.querySelectorAll('.btn-minus').forEach(btn => {
                btn.addEventListener('click', function (e) {
                    e.stopPropagation();
                    const index = this.getAttribute('data-index');
                    if (cart[index].qty > 1) {
                        cart[index].qty--;
                    } else {
                        cart.splice(index, 1);
                    }
                    renderSidebar();
                });
            });

            itemsList.querySelectorAll('.btn-plus').forEach(btn => {
                btn.addEventListener('click', function (e) {
                    e.stopPropagation();
                    const index = this.getAttribute('data-index');
                    if (cart[index].qty < 99) {
                        cart[index].qty++;
                    }
                    renderSidebar();
                });
            });

            itemsList.querySelectorAll('.ksv-remove-item-btn').forEach(btn => {
                btn.addEventListener('click', function (e) {
                    e.stopPropagation();
                    const index = this.getAttribute('data-index');
                    cart.splice(index, 1);
                    renderSidebar();
                });
            });
        }

        cards.forEach(card => {
            card.addEventListener('click', function (e) {
                if (e.target.closest('a') || e.target.closest('button') || e.target.closest('.ksv-qty-controls')) return;

                const id = this.getAttribute('data-id');
                const name = this.getAttribute('data-name');
                const price = this.getAttribute('data-price');

                const findIndex = cart.findIndex(item => item.id === id);

                if (findIndex !== -1) {
                    cart[findIndex].qty++;
                } else {
                    cart.push({id, name, price, qty: 1});
                }
                renderSidebar();
            });
        });

        form.addEventListener('submit', function (e) {
            form.querySelectorAll('.ksv-js-hidden-data').forEach(el => el.remove());
            cart.forEach((item, index) => {
                const inputId = document.createElement('input');
                inputId.type = 'hidden';
                inputId.className = 'ksv-js-hidden-data';
                inputId.name = `items[${index}].maDichVu`;
                inputId.value = item.id;

                const inputQty = document.createElement('input');
                inputQty.type = 'hidden';
                inputQty.className = 'ksv-js-hidden-data';
                inputQty.name = `items[${index}].soLuong`;
                inputQty.value = item.qty;

                form.appendChild(inputId);
                form.appendChild(inputQty);
            });
        });
    }

    function initNewsletterSubscribe() {
        const footerEmailInput = document.getElementById('footerEmail');
        const footerForm = document.querySelector('.ksv-newsletter');

        if (footerForm && footerEmailInput) {
            footerForm.addEventListener('submit', function (e) {
                e.preventDefault();

                const emailVal = footerEmailInput.value.trim();
                if (!emailVal) {
                    alert('Vui lòng nhập địa chỉ email hợp lệ!');
                    return;
                }

                const token = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
                const header = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

                const headers = {'Content-Type': 'application/json'};
                if (token && header) {
                    headers[header] = token;
                }

                fetch('/api/newsletter/subscribe', {
                    method: 'POST',
                    headers: headers,
                    body: JSON.stringify({email: emailVal})
                })
                    .then(res => {
                        if (!res.ok) throw new Error('Lỗi máy chủ');
                        return res.json();
                    })
                    .then(data => {
                        if (data && (data.success || data.status === "success" || data.message)) {
                            alert(data.message || 'Đăng ký thành công! Vui lòng kiểm tra hộp thư ảo tại Mailtrap.');
                            footerEmailInput.value = '';
                        } else {
                            alert('Hệ thống không phản hồi kết quả, vui lòng thử lại.');
                        }
                    })
                    .catch(err => {
                        console.error(err);
                        alert('Không thể kết nối tới hệ thống máy chủ lúc này!');
                    });
            });
        }
    }

    const toasts = document.querySelectorAll('.ksv-toast');
    toasts.forEach(toast => {
        setTimeout(() => {
            toast.style.animation = 'ksvFadeOut 0.4s ease forwards';
            setTimeout(() => {
                toast.remove();
            }, 400);
        }, 4000);
    });

    window.addEventListener('pageshow', function (event) {
        if (event.persisted || (window.performance && window.performance.navigation.type === 2)) {
            const btnApDung = document.getElementById('btnApDung');
            const inputMa = document.getElementById('inputMaGiamGia');

            if (btnApDung) {
                btnApDung.removeAttribute('data-applied');
                btnApDung.disabled = false;
            }
            if (inputMa) {
                inputMa.value = '';
                inputMa.readOnly = false;
                inputMa.disabled = false;
            }

            const inputNgayNhan = document.getElementById('ngayNhan');
            if (inputNgayNhan) {
                inputNgayNhan.dispatchEvent(new Event('change'));
            }
        }
    });

    startCountdowns();
    initCarousels();
    initHeaderScroll();
    initStarRating();
    initDetailTabsScroll();
    initBookingSidebarSync();
    initPromoCodeHandler();
    initFormValidation();
    initAboutSlider();
    initServiceDetail();
    initServiceSidebarCart();
    initNewsletterSubscribe();

    setTimeout(updateFlashSalesRealtime, 2000);
    setInterval(updateFlashSalesRealtime, 10000);
});