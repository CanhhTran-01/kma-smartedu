-- ==========================================
-- TAO BANG INTERNSHIP PERIOD
-- ==========================================

CREATE TABLE internship_periods (
    -- Base
                                    id BIGSERIAL PRIMARY KEY,
                                    created_at TIMESTAMP,
                                    updated_at TIMESTAMP,
                                    created_by VARCHAR(255),
                                    updated_by VARCHAR(255),
                                    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,

    -- InternshipPeriod
                                    internship_type VARCHAR(50) NOT NULL,
                                    name VARCHAR(150) NOT NULL,
                                    start_date DATE NOT NULL,
                                    end_date DATE NOT NULL,
                                    registration_deadline DATE
);

-- ==========================================
-- UNIQUE NAME - CHI AP DUNG CHO RECORD ACTIVE
-- ==========================================
-- Cho phep tao lai period co cung ten sau khi
-- period cu da soft-delete (is_deleted = TRUE).

CREATE UNIQUE INDEX uk_internship_periods_name_active
    ON internship_periods (name)
    WHERE is_deleted = FALSE;


-- ==========================================
-- INSERT DU LIEU MAU
-- ==========================================

INSERT INTO internship_periods (
    internship_type,
    name,
    start_date,
    end_date,
    registration_deadline,
    created_at,
    updated_at,
    created_by,
    updated_by,
    is_deleted
) VALUES
      (
          'BASIC',
          'Thực tập cơ sở HK1 2025-2026',
          '2025-10-01',
          '2025-12-31',
          '2025-09-25',
          NOW(),
          NOW(),
          'admin',
          'admin',
          FALSE
      ),
      (
          'GRADUATION',
          'Thực tập tốt nghiệp HK2 2025-2026',
          '2026-03-01',
          '2026-05-31',
          '2026-02-25',
          NOW(),
          NOW(),
          'admin',
          'admin',
          FALSE
      ),
      (
          'BASIC',
          'Thực tập cơ sở HK1 2026-2027',
          '2026-10-01',
          '2026-12-31',
          '2026-09-25',
          NOW(),
          NOW(),
          'admin',
          'admin',
          FALSE
      ),
      (
          'GRADUATION',
          'Thực tập tốt nghiệp HK1 2026-2027',
          '2026-10-01',
          '2027-01-10',
          '2026-09-20',
          NOW(),
          NOW(),
          'admin',
          'admin',
          FALSE
      );