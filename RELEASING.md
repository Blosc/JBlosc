# Releasing Blosc

| Author | Contact | Date |
|--------|---------|------|
| Francesc Alted, Andrés Alted, Alberto Sabater | francesc@blosc.org | 2017-07-07 |

## Preliminaries

* Make sure that `RELEASE_NOTES.md` and `ANNOUNCE.md` are up to date with the latest news in the release.

* Check that tag `<version>` in pom.xml is updated. 

* Commit the changes:

```console
    $ git commit -a -m"Getting ready for X.Y.Z release"
```

## Testing

```console
  $ mvn clean install
```

## Tagging

* Create a tag ``X.Y.Z`` from ``master``.  Use the next message:

```console
    $ git tag -a vX.Y.Z -m "Tagging version X.Y.Z"
```

* Push the previous commits and tag to the github repo:

```console
    $ git push
    $ git push --tags
```

## Announcing

* Send an announcement to the blosc and comp.compression lists.  Use the ``ANNOUNCE.md`` file as skeleton (possibly as the definitive version).

## Post-release actions

* Edit tag `<version>` in pom.xml to increment the version to the next minor one (i.e. X.Y.Z --> X.Y.(Z+1).dev).

* Create new headers for adding new features in ``RELEASE_NOTES.md`` and add this place-holder instead:

  #XXX version-specific blurb XXX#

* Commit the changes:

```console
    $ git commit -a -m"Post X.Y.Z release actions done"
    $ git push
```

That's all folks!