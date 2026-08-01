-- ==========================================
-- 1. TẠO CÁC BẢNG (TABLES)
-- ==========================================

-- Tạo bảng faculty (Kế thừa các trường từ BaseEntity)[cite: 1]
CREATE TABLE faculty (
                         id BIGSERIAL PRIMARY KEY,
                         created_at TIMESTAMP,
                         updated_at TIMESTAMP,
                         created_by VARCHAR(255),
                         updated_by VARCHAR(255),
                         is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                         faculty_code VARCHAR(20) NOT NULL UNIQUE,
                         faculty_name VARCHAR(200) NOT NULL,
                         description VARCHAR(1000),
                         is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- Tạo bảng major (Có khóa ngoại liên kết với faculty)[cite: 1]
CREATE TABLE major (
                       id BIGSERIAL PRIMARY KEY,
                       created_at TIMESTAMP,
                       updated_at TIMESTAMP,
                       created_by VARCHAR(255),
                       updated_by VARCHAR(255),
                       is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                       faculty_id BIGINT NOT NULL,
                       major_code VARCHAR(20) NOT NULL UNIQUE,
                       major_name VARCHAR(200) NOT NULL,
                       description VARCHAR(500),
                       CONSTRAINT fk_major_faculty FOREIGN KEY (faculty_id) REFERENCES faculty(id)
);

-- ==========================================
-- 2. INSERT DỮ LIỆU MẪU (FACULTIES)
-- ==========================================
INSERT INTO faculty (faculty_code, faculty_name, description, created_at, created_by)
VALUES
('CNTT', 'Công nghệ thông tin', 'Đào tạo cử nhân, kỹ sư Công nghệ thông tin', CURRENT_TIMESTAMP, 'system_setup'),
('ATTT', 'An toàn thông tin', 'Đào tạo kỹ sư An toàn thông tin, Mật mã', CURRENT_TIMESTAMP, 'system_setup'),
('DTVT', 'Điện tử viễn thông', 'Đào tạo kỹ sư Điện tử - Viễn thông', CURRENT_TIMESTAMP, 'system_setup'),
('CB', 'Khoa Cơ bản', 'Giảng dạy các môn khoa học cơ bản (Toán, Lý, Hóa...)', CURRENT_TIMESTAMP, 'system_setup'),
('LLCT', 'Lý luận chính trị', 'Giảng dạy các môn Lý luận chính trị, Mác-Lênin', CURRENT_TIMESTAMP, 'system_setup'),
('QSQP', 'Giáo dục Quốc phòng', 'Giảng dạy kiến thức Giáo dục Quốc phòng - An ninh', CURRENT_TIMESTAMP, 'system_setup'),
('GDTC', 'Giáo dục thể chất', 'Giảng dạy các môn Giáo dục thể chất, Thể dục thể thao', CURRENT_TIMESTAMP, 'system_setup'),
('NN', 'Ngoại ngữ', 'Giảng dạy Tiếng Anh, Tiếng chuyên ngành', CURRENT_TIMESTAMP, 'system_setup'),
('KHMT', 'Khoa học máy tính', 'Nghiên cứu và đào tạo chuyên sâu Khoa học máy tính', CURRENT_TIMESTAMP, 'system_setup'),
('KTPM', 'Kỹ thuật phần mềm', 'Đào tạo kỹ sư chuyên ngành Kỹ thuật phần mềm', CURRENT_TIMESTAMP, 'system_setup'),
('HTTT', 'Hệ thống thông tin', 'Đào tạo chuyên ngành Hệ thống thông tin quản lý', CURRENT_TIMESTAMP, 'system_setup'),
('KTMT', 'Kỹ thuật máy tính', 'Đào tạo kỹ sư phần cứng và Kỹ thuật máy tính', CURRENT_TIMESTAMP, 'system_setup'),
('MM', 'Mật mã học', 'Nghiên cứu và đào tạo chuyên sâu về hệ mật mã', CURRENT_TIMESTAMP, 'system_setup'),
('QTKD', 'Quản trị kinh doanh', 'Đào tạo cử nhân Quản trị kinh doanh', CURRENT_TIMESTAMP, 'system_setup'),
('KT', 'Kế toán', 'Đào tạo cử nhân Kế toán doanh nghiệp', CURRENT_TIMESTAMP, 'system_setup'),
('TC', 'Tài chính - Ngân hàng', 'Đào tạo cử nhân Tài chính - Ngân hàng', CURRENT_TIMESTAMP, 'system_setup'),
('TKDH', 'Thiết kế đồ họa', 'Đào tạo cử nhân Mỹ thuật công nghiệp, Thiết kế đồ họa', CURRENT_TIMESTAMP, 'system_setup'),
('TTĐPT', 'Truyền thông Đa phương tiện', 'Đào tạo cử nhân Truyền thông đa phương tiện', CURRENT_TIMESTAMP, 'system_setup'),
('TMDT', 'Thương mại điện tử', 'Đào tạo cử nhân Thương mại điện tử', CURRENT_TIMESTAMP, 'system_setup'),
('AI', 'Trí tuệ nhân tạo', 'Đào tạo kỹ sư chuyên ngành Trí tuệ nhân tạo và Dữ liệu', CURRENT_TIMESTAMP, 'system_setup');

-- ==========================================
-- 3. INSERT DỮ LIỆU MẪU (MAJORS)
-- ==========================================
INSERT INTO major (faculty_id, major_code, major_name, description, created_at, created_by)
VALUES
((SELECT id FROM faculty WHERE faculty_code = 'CNTT'), 'CNTT_CLO', 'Công nghệ thông tin chất lượng cao', 'Chương trình đào tạo chuẩn quốc tế', CURRENT_TIMESTAMP, 'system_setup'),
((SELECT id FROM faculty WHERE faculty_code = 'CNTT'), 'CNTT_UD', 'Công nghệ thông tin ứng dụng', 'Ứng dụng CNTT trong doanh nghiệp', CURRENT_TIMESTAMP, 'system_setup'),
((SELECT id FROM faculty WHERE faculty_code = 'ATTT'), 'ATTT_MM', 'An toàn dữ liệu và Mật mã', 'Chuyên sâu về mã hóa và bảo mật', CURRENT_TIMESTAMP, 'system_setup'),
((SELECT id FROM faculty WHERE faculty_code = 'ATTT'), 'ANM_QT', 'An ninh mạng quốc tế', 'Phòng chống tấn công mạng nâng cao', CURRENT_TIMESTAMP, 'system_setup'),
((SELECT id FROM faculty WHERE faculty_code = 'DTVT'), 'VT_VTQ', 'Viễn thông và Quản mạng', 'Đào tạo kỹ sư hạ tầng mạng viễn thông', CURRENT_TIMESTAMP, 'system_setup'),
((SELECT id FROM faculty WHERE faculty_code = 'KHMT'), 'KHMT_AI', 'Trí tuệ nhân tạo và Học máy', 'Chuyên sâu về AI, Deep Learning', CURRENT_TIMESTAMP, 'system_setup'),
((SELECT id FROM faculty WHERE faculty_code = 'KHMT'), 'KHMT_DL', 'Khoa học dữ liệu', 'Phân tích và khai phá dữ liệu lớn', CURRENT_TIMESTAMP, 'system_setup'),
((SELECT id FROM faculty WHERE faculty_code = 'KTPM'), 'KTPM_WEB', 'Phát triển Web Fullstack', 'Thiết kế và lập trình ứng dụng web', CURRENT_TIMESTAMP, 'system_setup'),
((SELECT id FROM faculty WHERE faculty_code = 'KTPM'), 'KTPM_MOB', 'Lập trình thiết bị di động', 'Phát triển ứng dụng iOS và Android', CURRENT_TIMESTAMP, 'system_setup'),
((SELECT id FROM faculty WHERE faculty_code = 'HTTT'), 'HTTT_DN', 'Hệ thống thông tin doanh nghiệp', 'Quản trị hệ thống ERP, CRM', CURRENT_TIMESTAMP, 'system_setup'),
((SELECT id FROM faculty WHERE faculty_code = 'QTKD'), 'QTKD_MKT', 'Quản trị Marketing', 'Chuyên ngành Marketing số', CURRENT_TIMESTAMP, 'system_setup'),
((SELECT id FROM faculty WHERE faculty_code = 'QTKD'), 'QTKD_QT', 'Quản trị kinh doanh quốc tế', 'Kinh doanh toàn cầu', CURRENT_TIMESTAMP, 'system_setup'),
((SELECT id FROM faculty WHERE faculty_code = 'TC'), 'TC_NH', 'Tài chính Ngân hàng số', 'Công nghệ tài chính (Fintech)', CURRENT_TIMESTAMP, 'system_setup');