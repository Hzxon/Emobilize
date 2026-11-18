-- Emobilize cloud tables. Run once in the Supabase SQL editor.
-- Each table mirrors a Room table in core/data/local/EmobilizeDatabase.kt. Rows belong to the
-- signed-in user: user_id defaults to auth.uid(), and row level security hides other users' rows.
-- Deletes are soft (deleted = true) so they reach every device; the app never deletes a row.

create table public.categories (
    user_id uuid not null default auth.uid() references auth.users on delete cascade,
    id bigint not null,
    name text not null,
    color integer not null,
    code text not null,
    updated_at bigint not null,
    deleted boolean not null default false,
    primary key (user_id, id)
);

create table public.tasks (
    user_id uuid not null default auth.uid() references auth.users on delete cascade,
    id bigint not null,
    name text not null,
    category_id bigint,
    date date,
    time text,
    priority text,
    range_start_date date,
    range_start_time text,
    range_end_date date,
    range_end_time text,
    event_type text not null,
    event_end_time text,
    reminder text not null,
    description text not null default '',
    estimate_minutes integer,
    status text not null,
    updated_at bigint not null,
    deleted boolean not null default false,
    primary key (user_id, id)
);

create table public.blocks (
    user_id uuid not null default auth.uid() references auth.users on delete cascade,
    id bigint not null,
    date date not null,
    title text not null,
    start_hour real not null,
    end_hour real not null,
    category_id bigint not null,
    reminder text not null,
    updated_at bigint not null,
    deleted boolean not null default false,
    primary key (user_id, id)
);

alter table public.categories enable row level security;
alter table public.tasks enable row level security;
alter table public.blocks enable row level security;

create policy "Own categories" on public.categories for all to authenticated
    using ((select auth.uid()) = user_id) with check ((select auth.uid()) = user_id);
create policy "Own tasks" on public.tasks for all to authenticated
    using ((select auth.uid()) = user_id) with check ((select auth.uid()) = user_id);
create policy "Own blocks" on public.blocks for all to authenticated
    using ((select auth.uid()) = user_id) with check ((select auth.uid()) = user_id);

-- The app never deletes rows, so the signed-in role only needs read, insert and update.
grant select, insert, update on public.categories, public.tasks, public.blocks to authenticated;
