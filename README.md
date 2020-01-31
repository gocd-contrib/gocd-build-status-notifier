# GoCD build status notifier  [![Build Status](https://snap-ci.com/gocd-contrib/gocd-build-status-notifier/branch/master/build_image)](https://snap-ci.com/gocd-contrib/gocd-build-status-notifier/branch/master)

This is GoCD's Notification plugin that updates SCM with build status.

Supported:
* GitHub Pull Request status
* Stash Pull Request status
* Gerrit Change Set status

## Requirements
These plugins require GoCD version >= v15.x or above

## Get Started
**Installation:**
- Download the latest plugin jar from [Releases](https://github.com/srinivasupadhya/gocd-build-status-notifier/releases) section. Place it in `<go-server-location>/plugins/external` & restart Go Server.

## Behavior
- Go Server notifies the plugin on every `Stage Status Change` with relevant details. The plugin scans the `build-cause` to see if the `github.pr` / `stash.pr` / `gerrit.cs` material is present.
- If it is, then Pull Request/Change Set status is updated with `status=stage-result`, `context=pipeline-name/stage-name` & `target-url=trackback-url`.

## Configuration

- You will see `Github Pull Requests status notifier` / `Stash Pull Requests status notifier` / `Gerrit Change Set status notifier` / `GitLab Feature Branch status notifier`  on plugin listing page
![Plugins listing page][1]

- You can configure the plugin (this feature requires GoCD version >= v15.2, use system properties to configure the plugin). The details should be as follows:
  - **Server Base URL** The URL of your go-server (by default, http://localhost:8153).
  - **End Point** If using enterprise edition, this needs to be configured for the enterprise endpoint. Else, it can be left blank.
  - **Username** A Github user with push access to the repository.
  - **Password** If not using two factor authentication, this is the password for the given Github user. Else, it can be left blank.
  - **OAuth Token** If using two-factor authentication, provide a personal access token (with the `repo:status` oath scope) instead of the password above. Else, it can be left blank.

![Configure plugin pop-up][2]

- When the stage status changes...
![Pipeline Schedule][3]

- The PR state is updated!
![Update Status][4]

**Target URL:**
- You can provide `trackback` through system property `go.plugin.build.status.go-server`. It defaults to 'http://localhost:8153' if not available.
Eg:
```
-Dgo.plugin.build.status.go-server=http://localhost:8153
```

#### GitHub
**Authentication:**
- You can choose to provide `username` & `password` through system property `go.plugin.build.status.github.username` & `go.plugin.build.status.github.password`.
Eg:
```
-Dgo.plugin.build.status.github.username=johndoe
-Dgo.plugin.build.status.github.password=thisaintapassword
```
or provide the `oauth access token` through system property `go.plugin.build.status.github.oauth`.
```
-Dgo.plugin.build.status.github.username=johndoe
-Dgo.plugin.build.status.github.oauth=thisaintatoken
```

- (or) through file `~/.github` on Go Server with the following contents:
```
login=johndoe
password=thisaintapassword
```
or
```
login=johndoe
oauth=thisaintatoken
```

**Github Enterprise:**
- You can choose to provide `endpoint` through system property `go.plugin.build.status.github.endpoint`.
Eg:
```
-Dgo.plugin.build.status.github.endpoint=http://code.yourcompany.com
```
- (or) through file `~/.github` on Go Server with the following contents:
```
endpoint=http://code.yourcompany.com
```

#### Stash
**Setup:**
- You need to provide `endpoint`, `username` & `password` through system property `go.plugin.build.status.stash.endpoint`, `go.plugin.build.status.stash.username` & `go.plugin.build.status.stash.password`.
Eg:
```
-Dgo.plugin.build.status.stash.endpoint=http://localhost:7990
-Dgo.plugin.build.status.stash.username=johndoe
-Dgo.plugin.build.status.stash.password=thisaintapassword
```

#### Gerrit
**Setup:**
- You need to provide `endpoint`, `username`, `password` & `codeReviewLabel` through system property `go.plugin.build.status.gerrit.endpoint`, `go.plugin.build.status.gerrit.username`, `go.plugin.build.status.gerrit.password`, `go.plugin.build.status.gerrit.codeReviewLabel`.
Eg:
```
-Dgo.plugin.build.status.gerrit.endpoint=http://localhost:7990
-Dgo.plugin.build.status.gerrit.username=johndoe
-Dgo.plugin.build.status.gerrit.password=thisaintapassword
-Dgo.plugin.build.status.gerrit.codeReviewLabel=Verified
```

#### Gitlab
**Setup:**
- This works with git.fb poller plugin that can be found here https://github.com/ashwanthkumar/gocd-build-github-pull-requests/releases
- You will need a Gitlab Oauth token: https://docs.gitlab.com/ee/user/profile/personal_access_tokens.html
- You need to provide `server_base_url`, `endpoint`, `oauth_token` using the plugin configuration view
![Configure Gitlab Plugin][5]
- Alternatively you can pass them through GoCD system property `go.plugin.build.status.gitlab.endpoint`, `go.plugin.build.status.gitlab.oauth`.
Eg:
```
-Dgo.plugin.build.status.gitlab.endpoint=http://gitlab.com
-Dgo.plugin.build.status.gitlab.oauth=XXXX
```

## FAQs

[1]: images/list-plugin.png  "List Plugin"
[2]: images/configure-plugin.png  "Configure Plugin"
[3]: images/pipeline-schedule.png  "Pipeline Schedule"
[4]: images/update-status.png  "On Successful Status Update"
[5]: images/gitlab-plugin-settings.png  "Configure Gitlab Plugin"
