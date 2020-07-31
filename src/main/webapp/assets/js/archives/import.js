const Archive = {
    clear() {
        this.Category = [];
        this.Document = [];
        this.Debate = [];
        this.Comment = [];
        this.Institution = [];
        this.Notification = [];
        this.Team = [];
        this.User = [];
    },
    get(id, constructor) {
        if (this.hasOwnProperty(constructor.name)) {
            let index = this[constructor.name].findIndex(o => o.id === id * 1);
            if (index === -1) {
                return -1;
            }
            return this[constructor.name][index];
        }
        return -1;
    },
    getOrCreate(obj, constructor) {
        let id = obj.id || obj.categoryId || obj.documentId || obj.debateId || obj.annotationId || obj.groupId || obj.creationDate || false;
        let fetched = this.get(id, constructor);
        if (fetched === -1) {
            fetched = this.convert[constructor.name](obj);
            this[constructor.name].push(fetched);
        }
        return fetched;

    },
    convert: {
        Category: function (old) {
            let category = new Category();
            category.id = old.categoryId;
            category.name = old.name;
            category.picture = old.image;
            category.documentsCount = old.documentsCount;
            category.debatesCount = old.debatesCount;
            category.lastCommentDate = new Date(old.lastPostDate);
            category.documents = [];
            return category;
        },
        Document: function (old) {
            let document = new Document();
            document.id = old.documentId;
            document.created = new Date(old.creationDate);
            document.name = old.title;
            document.author = old.author;
            document.content = old.text;
            document.debatesCount = 0;
            document.lastCommentDate = new Date(0);
            document.debates = [];
            return document;
        },
        Debate: function (old) {
            let debate = new Debate();
            debate.id = old.debateId;
            debate.created = new Date(old.openDate);
            debate.name = old.name;
            debate.closed = old.closedDate;
            debate.active = !old.isClosed;
            debate.commentsCount = 0;
            debate.commentsCountFor = 0;
            debate.commentsCountAgainst = 0;
            debate.lastCommentDate = new Date(old.lastPostDate);
            debate.guests = [];
            debate.owner = new User();
            debate.document = Archive.getOrCreate(old.document, Document);
            if (!debate.document.debates.includes(debate)) {
                debate.document.debates.push(debate);
            }
            debate.document.category = Archive.getOrCreate(old.categoriesList[0], Category);
            if (!debate.document.category.documents.includes(debate.document)) {
                debate.document.category.documents.push(debate.document);
            }
            let len = old.groupsList.length;
            for (let i = 0; i < len; i++) {
                let team = Archive.getOrCreate(old.groupsList[i], Team);
                if (!debate.teams.includes(team)) {
                    debate.teams.push(team);
                }
                if (!team.debates.includes(debate)) {
                    team.debates.push(debate);
                }
            }
            len = old.annotationsList[0].childrenAnnotationsList.length;
            for (let i = 0; i < len; i++) {
                let comment = Archive.getOrCreate(old.annotationsList[0].childrenAnnotationsList[i], Comment);
                comment.startContainer = comment.startContainer.split("/");
                comment.startContainer[0] -= 1;
                comment.startContainer = comment.startContainer.join("/");
                comment.endContainer = comment.endContainer.split("/");
                comment.endContainer[0] -= 1;
                comment.endContainer = comment.endContainer.join("/");
                if (!debate.comments.includes(comment)) {
                    debate.comments.push(comment);
                }
            }
            debate.comments.forEach(function flaten(c) {
                if (!debate.comments.includes(c)) {
                    debate.comments.push(c);
                }
                debate.commentsCount++;
                debate.commentsCountFor += c.opinion === "FOR" ? 1 : 0;
                debate.commentsCountAgainst += c.opinion === "AGAINST" ? 1 : 0;
                c.debate = debate;
                c.comments.forEach(flaten);
            });
            return debate;
        },
        Comment: function (old) {
            let comment = new Comment();
            comment.id = old.annotationId;
            comment.created = new Date(old.creationDate);
            comment.reformulation = old.reformulation;
            comment.argumentation = old.argumentation;
            comment.selection = old.annotationText;
            comment.tags = [];
            // trick to compare positions
            let start = old.annotationStart.split("/").reverse().map((x, i) => x * Math.pow(10, (i + 1) * 5)).reduce((acc, val) => acc + val) + old.annotationStartOffset;
            let end = old.annotationEnd.split("/").reverse().map((x, i) => x * Math.pow(10, (i + 1) * 5)).reduce((acc, val) => acc + val) + old.annotationEndOffset;
            if (start < end) {
                comment.startContainer = old.annotationStart;
                comment.startOffset = old.annotationStartOffset;
                comment.endContainer = old.annotationEnd;
                comment.endOffset = old.annotationEndOffset;
            } else {
                comment.startContainer = old.annotationEnd;
                comment.startOffset = old.annotationEndOffset;
                comment.endContainer = old.annotationStart;
                comment.endOffset = old.annotationStartOffset;
            }
            comment.opinion = old.adviseGrade === 1 ? Opinion.AGAINST : old.adviseGrade === 2 ? Opinion.FOR : Opinion.NEUTRAL;
            comment.signaled = false;
            comment.moderated = false;
            comment.owner = Archive.getOrCreate(old.user, User);
            if (!comment.owner.comments.includes(comment)) {
                comment.owner.comments.push(comment);
            }
            let len = old.childrenAnnotationsList.length;
            for (let i = 0; i < len; i++) {
                let child = Archive.getOrCreate(old.childrenAnnotationsList[i], Comment);
                if (!comment.comments.includes(child)) {
                    comment.comments.push(child);
                }
                child.parent = comment;
            }
            return comment;
        },
        Team: function (old) {
            let team = new Team();
            team.id = old.groupId;
            team.entId = old.entId;
            team.name = old.name;
            team.community = false;
            team.debatesCount = old.openDebatesCount;
            team.usersCount = 0;
            team.users = [];
            return team;
        },
        User: function (old) {
            let user = new User();
            user.id = old.creationDate;
            user.entId = old.entId;
            user.username = old.username;
            user.firstName = old.firstName;
            user.lastName = old.lastName;
            user.email = old.email;
            user.lastLogin = old.lastLoginDate;
            user.active = true;
            user.authority = old.roleGrade === 1 ? Authority.MODO : old.roleGrade === 0 ? Authority.ADMIN : Authority.USER;
            return user;
        }
    },

    importation(callback) {
        let list = [];
        let urls = ['2.json', '3.json', '4.json', '7.json', '8.json', '9.json', '10.json', '11.json', '12.json', '13.json', '14.json', '15.json'];

        Archive.clear();

        urls.forEach(function (url, i) {
            list.push(
                    fetch("assets/js/archives/" + url)
                    .then(function (response) {
                        return response.ok ? response.json() : false
                    })
                    .then(function (data) {
                        if (data) {
                            let leng = data.length;
                            for (let k = 0; k < leng; k++) {
                                Archive.getOrCreate(data[k], Debate);
                            }
                        }
                    }));
        });

        Promise.all(list)
                .then(function () {
                    if (callback) {
                        callback();
                    }
                });
    }
};
Archive.clear();