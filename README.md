# GoCD build status notifier
This is GoCD's Notification plugin that updates SCM with build status.

Supported:
* GitHub Pull Request status

To Do:
* Stash Pull Request status
* Gerrit Change Set status

## Requirements
This needs GoCD >= v15.x which is due release as of writing.

## Get Started
**Installation:**
- Download the latest plugin jar from [Releases](https://github.com/srinivasupadhya/gocd-build-status-notifier/releases) section. Place it in `<go-server-location>/plugins/external` & restart Go Server.

## Behavior
- Go Server notifies the plugin on every `Stage Status Change` with relevant details. The plugin scans the `build-cause` to see if the `github.pr` / `stash.pr` / `gerrit.cs` material is present.
- If it is, then Pull Request/Change Set status is updated with `status=stage-result`, `context=pipeline-name/pipeline-counter/stage-name/stage-counter` & `target-url=trackback-url`.

**Target URL:**
- You can provide `trackback` through system property `go.plugin.build.status.go-server`. It defaults to 'localhost:8153' if not available.
Eg:
```
-Dgo.plugin.build.status.go-server=localhost:8153
```

**Authentication:**

### GitHub
- You can choose to provide `username` & `password` through system property `go.plugin.build.status.github.username` & `go.plugin.build.status.github.password`.
Eg: 
```
-Dgo.plugin.build.status.github.username=johndoe
-Dgo.plugin.build.status.github.password=thisaintapassword
```
or provide the `oauth access token` through system property `go.plugin.build.status.github.oauthAccessToken`.
```
-Dgo.plugin.build.status.github.oauthAccessToken=thisaintatoken
```

- (or) through file `~/.github` on Go Server with the following contents:
```
login=johndoe
password=thisaintapassword
```
or
```
oauthAccessToken=thisaintatoken
```

- **Github Enterprise:** You can choose to provide `endpoint` through system property `go.plugin.build.status.github.endpoint`.
Eg:
```
-Dgo.plugin.build.status.github.endpoint=http://code.yourcompany.com
```
- (or) through file `~/.github` on Go Server with the following contents:
```
endpoint=http://code.yourcompany.com
```

## FAQs
