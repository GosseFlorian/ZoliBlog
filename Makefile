# Raccourcis DevOps — ZoliBlog
ifeq ($(OS),Windows_NT)
    MVNW = mvnw.cmd
else
    MVNW = ./mvnw
endif
.PHONY: help setup env db-init db-test lint format test test-coverage ci backend admin site

help:
	@echo "Commandes :"
	@echo "  make setup     - .env + npm install (admin + site)"
	@echo "  make env       - cp .env.example .env"
	@echo "  make db-init   - blog.sql"
	@echo "  make db-test   - crée java_blog_test"
	@echo "  make lint      - ESLint admin + site"
	@echo "  make format    - Prettier admin + site"
	@echo "  make test           - tests (backend + admin + site)"
	@echo "  make test-coverage  - tests + rapports de couverture (JaCoCo + Vitest)"
	@echo "  make ci             - tests + lint + build admin + build site"
	@echo "  make backend   - API port 8080"
	@echo "  make admin     - Vite port 5173"
	@echo "  make site      - Vite port 5174"

setup: env
	cd admin && npm install
	cd site && npm install

env:
	cp .env.example .env

db-init:
	PGPASSWORD=$${POSTGRES_PASSWORD:-postgres} psql -h localhost -U $${POSTGRES_USER:-postgres} -d java_blog -f doc/sql/blog.sql

db-test:
	PGPASSWORD=$${POSTGRES_PASSWORD:-postgres} psql -h localhost -U $${POSTGRES_USER:-postgres} -d postgres -f doc/sql/upgrade-06-01-create-java-blog-test.sql

lint:
	cd admin && npm run lint
	cd site && npm run lint

format:
	cd admin && npm run format
	cd site && npm run format

test:
	$(MVNW) test
	cd admin && npm run test
	cd site && npm run test

test-coverage:
	$(MVNW) test
	cd admin && npm run test:coverage
	cd site && npm run test:coverage

ci:
	$(MVNW) -B test
	cd admin && npm ci && npm run lint && npm run test && npm run build
	cd site && npm ci && npm run lint && npm run test && npm run build

backend:
	$(MVNW) spring-boot:run

admin:
	cd admin && npm run dev

site:
	cd site && npm run dev