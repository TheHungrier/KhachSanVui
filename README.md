# KhachSanVui

## Secure local configuration

- Copy `src/main/resources/application-example.properties` to your local, untracked override file (for example: `src/main/resources/application-local.properties`) and fill values from environment variables.
- `src/main/resources/application.properties` now uses environment placeholders for sensitive settings.
- Do **not** commit real credentials or API keys.

### Required environment variables

- `DB_USERNAME`
- `DB_PASSWORD`
- `MAIL_USERNAME`
- `MAIL_PASSWORD`
- `VNPAY_TMN_CODE`
- `VNPAY_HASH_SECRET`
- `GOOGLE_CLIENT_ID`
- `GOOGLE_CLIENT_SECRET`
- `FACEBOOK_CLIENT_ID`
- `FACEBOOK_CLIENT_SECRET`

Optional variables with safe defaults:
- `DB_URL`
- `MAIL_HOST`
- `MAIL_PORT`
- `VNPAY_PAY_URL`
- `VNPAY_RETURN_URL`

If any of these credentials were previously committed, rotate/revoke them immediately.
