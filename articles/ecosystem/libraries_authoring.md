---
title: "Library Development and Distribution"
layout: article
---

## About this guide

This short guide covers how to create your own typical pure Clojure
library and distribute it to the community via Clojars. It uses
Clojure 1.4 and Leiningen 2.0-previewX, and requires you have git
installed (though very little familiarity with git is required).

It's assumed that you're already somewhat familiar with Clojure. If
not, see the [Getting Started](getting_started.html) and
[Introduction](introduction.html) guides.

For the purposes of this guide, the library we'll be making is named
"[trivial-library-example](https://clojars.org/trivial-library-example)".




## Create the Project

Create your new library project. Names are usually hyphen-separated
lowercase words:

    lein new trivial-library-example
    cd trivial-library-example

Typical lein usage is `lein new <proj-type> <proj-name>`, but if you
leave out `<proj-type>` (as we've done above), lein defaults to
creating a library project for you.

Our trivial library example project will have a dependency on
[flatland's "useful"](https://clojars.org/org.flatland/useful)
library.

Open up our new project.clj file and make a few changes:

 1. Add our dependency (`[org.flatland/useful "0.9.0"]`) to the `:dependencies` vector.
 2. Remove "-SNAPSHOT" from version string.
 3. Write a short description.
 4. Add a url (if not a homepage, then where it's source is hosted online).
 5. If you're using a different license, change the value for `:license`.

Regarding your choice of license, probably the three most common for
Clojure libs (along with a grossly oversimplified blurb (by this
author) for each) are:

  * The [Eclipse Public License] (the default).
  * The [LGPL](http://www.gnu.org/licenses/lgpl.html) (focused most on
    code and additions always being free; includes language addressing
    s/w patent concerns). See the [FSF's recommendations] and their
    [instructions for use].
  * The [MIT] License (focused most on the user's freedom to do what
    they want with the code). The FSF calls this the ["Expat"
    License](http://directory.fsf.org/wiki/License:Expat).

[Eclipse Public License]: http://directory.fsf.org/wiki/License:EPLv1.0
[GPL]: http://www.gnu.org/licenses/gpl.html
[FSF's recommendations]: http://www.gnu.org/licenses/license-recommendations.html
[instructions for use]: http://www.gnu.org/licenses/gpl-howto.html
[MIT]: http://opensource.org/licenses/MIT

Whichever one you choose, update your project.clj (if necessary) to
reflect that choice and save the text of the license as a file named
"LICENSE" or "COPYING" in your project directory.


### A Note Regarding Project Naming

The top line of your project.clj includes something like `defproject
my-project-name`.  This means that your project has an *artifact-id*
of "my-project-name", but it also implies a *group-id* of
"my-project-name" (group-id = artifact-id).

The artifact-id is the name of your project. The group-id is used for
namespacing (not the same thing as Clojure namespaces) --- it
identifies to which group/organization a project belongs. Some
examples of group-id's: clojurewerkz, sonian, and org.*your-domain*.

Read more about groups at
<https://github.com/ato/clojars-web/wiki/Groups>.

You may choose to explicitly use a group-id for your project, if you
like. For example:

    (defproject org.my-domain/my-project-name ...
    ...)

Some authors like to use a single group-id for most of the libs they
publish. The maintainers of Clojars recommend using the default
"artifact-id = group-id" (as we've done with trivial-library-example)
for libraries intended for broad community use and for those which are
expected to live long enough to perhaps have different maintainers
over time. You are free to handle the matter as you wish.



## Update the README

Aside from providing a good overview, rationale, and introduction at
the top, you're encouraged to provide some usage examples as well.  A
link to the lib's (future) Clojars page (which we'll get to below)
might also be appreciated. Add acknowledgements near the end, if
appropriate.  Adjust the copyright and license info at the bottom of
the README as needed.

Lein provides you with a doc directory and a starter doc/intro.md
file. If you find that you have more to say than will comfortably fit
into the README.md, consider moving content into the doc dir.

Other goodies you might include in your README.md or doc/\*.md files:
tutorial, news, bugs, limitations, alternatives, troubleshooting,
configuration.

Note that you generally won't add hand-written API documentation into
your readme or other docs, as there are tools for creating that
directly from your source (discussed later).




## Create your project's local git repository

Before going much further, you probably want to get your project under
version control. Make sure you've got git installed and configured to
know your name and email address (i.e., that at some point you've run
`git config --global user.name "Your Name"` and `git config --global
user.email "your-email@somewhere.org"`).

Then, in your project dir, run:

    git init
    git add .
    git commit -m "The initial commit."

At any time after you've made changes and want to inspect them and
commit them to the repository:

    git diff
    git add -p
    git commit -m "The commit message."





## Write Tests

In test/trivial_library_example/core_test.clj, add tests as needed.
An example is provided in there to get you started.




## Write Code

Write code to make your tests pass.

Remember to add a note at the top of each file indicating copyright
and the license under which the code is distributed.




## Run Tests

In your project dir:

    lein test




## Commit any remaining changes

Before continuing to the next step, make sure all tests pass and
you've committed all your changes. Check to see the status of your
repo at any time with `git status` and view changes with `git diff`.




## Create github project and Upload there

This guide makes use of [github](https://github.com/) to host your
project code. If you don't already have a github account, create one,
then log into it. Github provides good documentation on how to [get
started](https://help.github.com/articles/set-up-git) and how to
[create an SSH key
pair](https://help.github.com/articles/generating-ssh-keys). If you
haven't already done so, get that set up before continuing.

Create a new repo there for your project using the icon/button/link
near the top-right.

> You will have your local repository, and also a remote duplicate of
> it at github.

For the repository name, use the same name as your project directory.
Provide a one-line description and hit "Create repository".

Once this remote repo has been created, follow the instructions on the
resulting page to "Push an existing repository from the command
line". You'll of course run the `git` commands from your project
directory:

    git remote add origin git@github.com:uvtc/trivial-library-example.git
    git push -u origin master

You can now access your online repo. For this tutorial, it's
<https://github.com/uvtc/trivial-library-example>.

Any changes you commit to your local repository can now be pushed
to the remote one at github:

```bash
# work work work
git add -p
git commit -m "commit message here"
git push
```


## Create a GPG key for signing your releases

You'll need to create a [gpg](http://www.gnupg.org/) key pair, which
will be used by lein to sign any release you make to Clojars. Make
sure you've got gpg installed and kick the tires:

    gpg --list-keys

(The first time that command is run, you'll see some notices about
it creating necessary files in your home dir.)

To create a key pair:

    gpg --gen-key

Take the default key type (RSA and RSA), and default key size (2048).
When asked for how long the key should remain valid, choose a year or
two. Give it your real name and email address. When it prompts you for
a comment, you might add one as it can be helpful if you have multiple
keys to keep track of. When prompted for a passphrase, come up with one
that is different from the one used with your ssh key.

When gpg has completed generating your keypair, you can have it list
what keys it knows about:

    gpg --list-keys

We'll use that public key in the next section.




## Upload to Clojars

If you don't already have an account at <https://clojars.org/>, create
one. After doing so, you'll need to supply your ssh and gpg public
keys to Clojars.  For the ssh public key, you can use the same one as
used with github. For the gpg public key, get it by running:

    gpg --export -a <your-key-id>

where `<your-key-id>` is in the output of `gpg --list-keys` (the
8-character part following the forward slash on the line starting with
"pub"). Copy/paste that output (including the "-----BEGIN PGP PUBLIC
KEY BLOCK-----" and "-----END PGP PUBLIC KEY BLOCK-----") into the
form on your Clojars profile page.

For more info on working with Clojars, see [the Clojars
wiki](https://github.com/ato/clojars-web/wiki/About).

Once your Clojars account is all set up, and it has your public keys,
upload your library to Clojars like so:

    lein deploy clojars

You will be asked for your (Clojars) username and password.

Then you'll be asked for your gpg passphrase. (You won't be asked for
your ssh passphrase because `lein deploy clojars` uses http rather
than scp --- though Clojars supports both.)

You should now be able to see your lib's Clojars page: for example,
<https://clojars.org/trivial-library-example>!





## Generate API docs (optional)

For larger library projects, you may want to automatically generate
API docs (from your docstrings). See
[codox](https://github.com/weavejester/codox). If your library project
is hosted at github, you can use [github
pages](http://pages.github.com/) to host the resulting docs.





## Announce (optional)

You're welcome to announce the availability of your new library
on the [Clojure Mailing List](https://groups.google.com/forum/?fromgroups#!forum/clojure).




## Make Updates to your library

Making updates to your lib follows the same pattern as described above:

```bash
# work test work test
# update version string in project.clj
git add -p
git commit
git push
lein deploy clojars
```

And optionally announce the release on the ML.



### Merging pull-requests

Note that if you receive a pull-request at github, you can easily
merge those changes into your project (right there, via the web page
describing the pull-request). Afterwards, update your local repo to
grab those changes as well:

    git pull



## See Also

For more detailed documentation on various aspects of the procedures
described here, see:

  * the [Clojars wiki](https://github.com/ato/clojars-web/wiki)
  * the
    [Leiningen tutorial](https://github.com/technomancy/leiningen/blob/master/doc/TUTORIAL.md)
    and [deploy
    guide](https://github.com/technomancy/leiningen/blob/master/doc/DEPLOY.md)



## Contributors

John Gabriele <jmg3000@gmail.com> (original author)
