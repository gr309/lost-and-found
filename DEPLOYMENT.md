# Database and local image storage

The backend uses MySQL locally and managed PostgreSQL on Render. Images are files on the API server; only the image URL is stored in the database. No external image-storage API is required.

## Local setup

Run `./scripts/start-local.sh`. This creates `.env.local` with generated credentials, starts an isolated MySQL 8.4 container and the API, and keeps existing `.env` settings untouched. API: `http://localhost:8080`; MySQL: `127.0.0.1:3307`. Images: `./uploads/items`. Database: Docker volume `lostfound-local_mysql-data`.

The scripts do not copy records or images from a previous hosted deployment. Existing remote image URLs in a database remain usable while their original host is available.

If you prefer an existing local MySQL server, create a `lostfound` schema and a dedicated user with privileges on that schema, then update `.env.local` with its host, port, username and password. Run `./scripts/run-local.sh`. Hibernate creates/updates tables at startup; it does not remove obsolete columns or migrate data from another database. Back up an existing schema before applying changes.

## Deploy to Render

1. Push these files to your Git repository.
2. In Render choose **New → Blueprint**, connect the repository, and select `render.yaml`.
3. Review the PostgreSQL database and paid API service before creating them. The API disk requires a paid web-service plan; review current pricing in Render.
4. Enter `CORS_ALLOWED_ORIGINS`: your frontend origin, such as `https://your-frontend.example`, without a trailing slash. Separate multiple origins with commas.
5. Deploy. The Blueprint generates a stable JWT secret, connects the API to managed PostgreSQL, and mounts the upload disk.
6. Wait for PostgreSQL initialization and API startup. If the first API attempt precedes database readiness, redeploy the API once PostgreSQL is ready.
7. Check `https://<api-service>.onrender.com/health/`, then register, sign in, create an item with an image, and open its image URL.

No production deployment is performed by the local setup scripts.

### Persistent storage configuration

| Service | Mount path | Stored content |
| --- | --- | --- |
| `lostfound-db` | Render-managed PostgreSQL disk | PostgreSQL data |
| `lostfound-api` | `/app/uploads` | Images (`UPLOAD_DIR=/app/uploads`, 1 GB disk) |

Render's default filesystem is ephemeral. Free web services cannot attach persistent disks. Keep the API disk attached; a local directory without a persistent disk loses uploaded files on restart/redeploy. Disks also limit the service to one instance and prevent zero-downtime deploys. See [Render persistent disks](https://render.com/docs/disks) and [Render Blueprint references](https://render.com/docs/blueprint-spec).

### Updating an existing Render API service

Either deploy the new Blueprint services or apply the same configuration to your existing service. Do not accidentally create a second production database when you intend to retain the old one.

- Set the database variables from the managed PostgreSQL service, or use the Blueprint to create them automatically.
- Remove a stale `DB_URL` if using these separate variables; `DB_URL` takes precedence. Also remove old `SPRING_DATASOURCE_*` overrides if present.
- Attach a disk at `/app/uploads` and set `UPLOAD_DIR=/app/uploads`.
- Keep a valid `JWT_SECRET` stable. Generate a new one with `openssl rand -base64 32` only when needed; changing it invalidates current tokens.
- Set `CORS_ALLOWED_ORIGINS` and health-check path `/health/`. Render supplies `PORT`.
- Remove the unused `RAPIDFILE_SECRET_KEY`. Redeploy the API.

The Blueprint uses [Render's documented environment references](https://render.com/docs/blueprint-spec) to supply the managed PostgreSQL connection. Keep database and API services in the same region.

## Image API

Create an item using `POST /api/items/` with a bearer token and multipart fields `title`, `description`, `type` (`LOST` or `FOUND`), `location`, `eventDate` (YYYY-MM-DD), and optional `image`.

- Accepted: actual JPEG/PNG images, at most 5 MB and 20 megapixels. Unsupported/corrupt images return 400; uploads over the byte limit return 413.
- Processing: 800×800 JPEG on a white background, centered without upscaling.
- Response: `imageUrl` such as `/uploads/items/<uuid>.jpg` (or null for no image).
- Display from a separate frontend: `new URL(item.imageUrl, API_BASE_URL).href`.
- Images are public GET/HEAD resources. Uploads and item deletion require authentication; deletion also requires ownership.
- Failed database transactions remove newly uploaded images. Successful item deletion removes its local image after the transaction commits. A failed deletion keeps the file. Legacy remote URLs are never treated as local paths.
- The current update endpoint changes item fields only; it does not replace images.

## Verify after deployment

1. Confirm `/health/` returns `{"status":"UP"}` without a token. This is a liveness endpoint; it is not a continuous database health probe.
2. Register and sign in, then create an item with an image.
3. Open the returned image URL against the API origin without a token; expect JPEG content.
4. Restart/redeploy the API, and confirm the item and image are still available.
5. Confirm claim and notification GET requests without a token return 401.
6. Delete an owned test item that has no claims; confirm its image returns 404.

Items with existing claims still follow the existing database foreign-key rules; their deletion can be rejected. Disk/database cleanup cannot be perfectly atomic across a process crash. Monitor logged cleanup failures and disk space.

## Backups

Back up both the database and the upload directory. Stop application writes while taking a coordinated backup when consistency matters. For local MySQL, this creates a logical dump without printing the password:

```bash
mkdir -p backups
chmod 700 backups
docker compose --env-file .env.local exec -T db sh -c \
  'MYSQL_PWD="$MYSQL_PASSWORD" exec mysqldump --no-tablespaces --single-transaction -u "$MYSQL_USER" "$MYSQL_DATABASE"' \
  > backups/lostfound.sql
tar -czf backups/uploads.tar.gz uploads
```

Protect backup files: they contain user data. On Render use PostgreSQL's logical backup tools and copy images from the mounted API disk. A database disk snapshot alone is not a reliable database backup. Never run `docker compose down -v` unless you intentionally want to delete local database data.
