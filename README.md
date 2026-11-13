# Ready Chime

Plays a chime when the game has finished loading (the title screen appears) and when a resource reload is done. Client-side only.

| Loader   | Minecraft                                                       | Needs |
|----------|-----------------------------------------------------------------|-------|
| Fabric   | 1.21.1 – 1.21.11, 26.1 – 26.3                                   |       |
| NeoForge | 1.21.1, 1.21.3 – 1.21.5, 1.21.8, 1.21.10, 1.21.11, 26.1.2 – 26.3 |       |

- `fabric/<mc>/`, `neoforge/<mc>/`: one small Gradle project per Minecraft version (`./gradlew -p 26.3 build` inside `fabric/`)
- `VERSION`: the mod version for every build
- `jars/`: the release jars; the Publish workflow uploads them to CurseForge

GitHub Actions builds every version on each push.

---

ゲームの読み込みが終わってタイトル画面が出たときと、リソースの再読み込みが終わったときにチャイムを鳴らすMODです。クライアント専用。

## License

MIT © Mya
