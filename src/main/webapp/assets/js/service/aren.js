const Authority = {
  DELETED: "DELETED",
  GUEST: "GUEST",
  USER: "USER",
  MODO: "MODO",
  ADMIN: "ADMIN",
  SUPERADMIN: "SUPERADMIN",
  _value: {
    DELETED: -1,
    GUEST: 0,
    USER: 1,
    MODO: 2,
    ADMIN: 3,
    SUPERADMIN: 4
  }
};

const Opinion = {
  FOR: "FOR",
  NEUTRAL: "NEUTRAL",
  AGAINST: "AGAINST"
};

const Hypostase = [
  ["EXPLANATION", "LAW", "PRINCIPLE", "THEORY", "BELIEF", "CONJECTURE", "HYPOTHESIS", "AXIOM", "DEFINITION"],
  ["QUALITATIVE", "VARIABLE", "OBJECT", "EVENT", "PHENOMENON", "DATA", "MODE", "DOMAIN"],
  ["QUANTITATIVE", "VARIATION", "VARIANCE", "APPROXIMATION", "VALUE", "CLUE", "INVARIANT", "DIMENSION"],
  ["STRUCTURAL", "STRUCTURE", "METHOD", "FORMALISM", "CLASSIFICATION", "PARADIGME"],
  ["DIFFICULTY", "APORIA", "PARADOXE", "PROBLEM"]
]

function Tag(obj = {}) {
  if (obj.constructor === String) {
    obj = obj.replace(/[^A-Za-zÀ-ÖØ-öø-ÿ0-9 \-\']/g, "").trim();
    let split = obj.split("|");
    if (split[1]) {
      this.power = split[1] * 1;
    } else {
      this.power = 0;
    }
    split[0] = split[0].trim();
    if (split[0][0] === '-') {
      this.negative = true;
      this.value = split[0].substring(1).trim();
    } else {
      this.negative = false;
      this.value = split[0];
    }
  } else {
    this.value = obj.value;
    this.negative = obj.negative;
    this.power = obj.power;
  }
}

function Message(obj = {}) {
  this.title = obj.title;
  this.message = obj.message;
  this.details = obj.details;
}

function Entity(obj = {}) {
  for (let attr in this.attrs) {
    this[attr] = obj[attr] ? obj[attr] : undefined;
  }
  for (let attr in this.manyToOne) {
    this[attr] = undefined;
  }
  for (let attr in this.oneToMany) {
    this[attr] = [];
  }
  for (let attr in this.manyToMany) {
    this[attr] = [];
  }
}

function Configuration(obj = {}) {
  Entity.call(this, obj);
}
Configuration.prototype.attrs = {
  id: Number,
  key: String,
  value: String,
};

function Category(obj = {}) {
  Entity.call(this, obj);
}
Category.prototype.attrs = {
  id: Number,
  name: String,
  picture: String,
  debatesCount: Number,
  lastCommentDate: Date
};
Category.prototype.oneToMany = {
  documents: [Document, "category"]
};
Category.prototype.debates = function () {
  return this.documents.map((document) => document.debates).flat();
};

function Document(obj = {}) {
  Entity.call(this, obj);
}
Document.prototype.attrs = {
  id: Number,
  created: Date,
  name: String,
  author: String,
  content: String,
  debatesCount: Number,
  lastCommentDate: Date
};
Document.prototype.oneToMany = {
  debates: [Debate, "document"]
};
Document.prototype.manyToOne = {
  category: [Category, "documents"]
};

function Debate(obj = {}) {
  Entity.call(this, obj);
}
Debate.prototype.attrs = {
  id: Number,
  created: Date,
  name: String,
  closed: Date,
  active: Boolean,
  commentsCount: Number,
  commentsCountFor: Number,
  commentsCountAgainst: Number,
  lastCommentDate: Date,
  withHypostases: Boolean,
  reformulationCheck: Boolean,
  idfixLink: Boolean,
  openPublic: Boolean
};
Debate.prototype.oneToMany = {
  comments: [Comment, "debate"]
};
Debate.prototype.manyToOne = {
  owner: [User, "createdDebates"],
  document: [Document, "debates"]
};
Debate.prototype.manyToMany = {
  teams: [Team, "debates"],
  guests: [User, "invitedDebates"]
};
Debate.prototype.deepSortComments = function (sortFunction) {
  this.comments.sort(sortFunction);
  let len = this.comments.length;
  for (let i = 0; i < len; i++) {
    this.comments[i].deepSortComments(sortFunction);
  }
};

function Comment(obj = {}) {
  Entity.call(this, obj);
}
Comment.prototype.attrs = {
  id: Number,
  created: Date,
  reformulation: String,
  argumentation: String,
  selection: String,
  startContainer: String,
  startOffset: Number,
  endContainer: String,
  endOffset: Number,
  opinion: String,
  signaled: Boolean,
  moderated: Boolean,
  hypostases: Array,
  proposedTags: Array,
  tags: Array
};
Comment.prototype.oneToMany = {
  comments: [Comment, "parent"]
};
Comment.prototype.manyToOne = {
  owner: [User, "comments"],
  debate: [Debate, "comments"],
  parent: [Comment, "comments"]
};
Comment.prototype.deepSortComments = function (sortFunction) {
  this.comments.sort(sortFunction);
  let len = this.comments.length;
  for (let i = 0; i < len; i++) {
    this.comments[i].deepSortComments(sortFunction);
  }
};
Comment.prototype.compareBoundaryPoints = function (type, comment) {
  return compareRange(type, this, comment);
};

function Institution(obj = {}) {
  Entity.call(this, obj);
}
Institution.prototype.attrs = {
  id: Number,
  type: String,
  name: String,
  academy: String
};
Institution.prototype.oneToMany = {
  users: [User, "institution"],
  teams: [Team, "institution"]
};

function Notification(obj = {}) {
  Entity.call(this, obj);
}
Notification.prototype.attrs = {
  id: Number,
  owner: Number,
  created: Date,
  content: Message,
  unread: Boolean,
  comment: Number,
  debate: Number
};

function Team(obj = {}) {
  Entity.call(this, obj);
}
Team.prototype.attrs = {
  id: Number,
  entId: String,
  name: String,
  community: Boolean,
  debatesCount: Number,
  usersCount: Number
};
Team.prototype.manyToOne = {
  institution: [Institution, "teams"]
};
Team.prototype.manyToMany = {
  debates: [Debate, "teams"],
  users: [User, "teams"]
};

function User(obj = {}) {
  Entity.call(this, obj);
  this.authority = Authority.GUEST
}
User.prototype.attrs = {
  id: Number,
  entId: String,
  username: String,
  password: String,
  firstName: String,
  lastName: String,
  email: String,
  lastLogin: Date,
  active: Boolean,
  authority: String
};
User.prototype.oneToMany = {
  comments: [Comment, "owner"],
  createdDebates: [Debate, "owner"],
  notifications: [Notification, "owner"]
};
User.prototype.manyToOne = {
  institution: [Institution, "users"]
};
User.prototype.manyToMany = {
  invitedDebates: [Debate, "guests"],
  teams: [Team, "users"]
};
User.prototype.fullName = function () {
  if (this.firstName && this.lastName) {
    return this.firstName + " " + this.lastName;
  } else {
    return this.username;
  }
};
User.prototype.is = function (authority) {
  return Authority._value[this.authority] >= Authority._value[authority];
};

ApiService = function (anUrl, locale) {
  let self = this;

  this.Configs = {};

  this.Store = {
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
    detach(obj) {
      this[obj.constructor.name].remove(obj);
    },
    remove(obj) {
      let manyToOne = obj.manyToOne;
      let oneToMany = obj.oneToMany;
      let manyToMany = obj.manyToMany;

      for (let foreignKey in manyToOne) {
        if (obj[foreignKey]) {
          obj[foreignKey][manyToOne[foreignKey][1]].remove(obj);
        }
      }
      for (let collection in oneToMany) {
        if (obj[collection]) {
          let len = obj[collection].length;
          for (let i = 0; i < len; i++) {
            delete obj[collection][i][oneToMany[collection][1]];
          }
        }
      }
      for (let collection in manyToMany) {
        if (obj[collection]) {
          let len = obj[collection].length;
          for (let i = 0; i < len; i++) {
            obj[collection][i][manyToMany[collection][1]].remove(obj);
          }
        }
      }
      this.detach(obj);
    },
    createOrUpdate(obj, constructor) {
      if (!isNaN(obj)) {
        return this.get(obj * 1, constructor);
      }
      let that = this.get(obj.id, constructor);
      if (that === -1) {
        that = new constructor();
        if (this.hasOwnProperty(constructor.name)) {
          this[constructor.name].push(that);
        }
      }

      let attrs = that.attrs;
      let manyToOne = that.manyToOne;
      let oneToMany = that.oneToMany;
      let manyToMany = that.manyToMany;

      for (let attr in attrs) {
        if (obj[attr] !== undefined) {
          switch (attrs[attr]) {
            case Boolean:
              that[attr] = !!obj[attr];
              break;
            case Number:
              that[attr] = obj[attr] * 1;
              break;
            case String:
              that[attr] = "" + obj[attr];
              break;
            case Date:
              that[attr] = new Date(obj[attr]);
              break;
            case Array:
              that[attr] = obj[attr];
              break;
            default:
              that[attr] = new attrs[attr](obj[attr]);
          }
        }
      }
      if (constructor === Comment && that.tags !== undefined) {
        // If same same value in Tags and ProposedTags array
        // Then point to the same object
        let len = that.tags.length;
        for (let i = 0; i < len; i++) {
          let pTag = that.proposedTags.find((t) => t.value === that.tags[i].value);
          if (pTag !== undefined) {
            that.tags[i] = pTag;
          }
        }
      }

      for (let foreignKey in manyToOne) {
        if (obj[foreignKey] !== undefined) {
          that[foreignKey] = this.createOrUpdate(obj[foreignKey], manyToOne[foreignKey][0]);
          if (manyToOne[foreignKey][1]) {
            let foreignCollection = that[foreignKey][manyToOne[foreignKey][1]];
            if (!foreignCollection.includes(that)) {
              foreignCollection.push(that);
            }
          }
        }
      }
      for (let collection in oneToMany) {
        if (obj[collection] !== undefined) {
          if (obj[collection] && obj[collection].length > 0) {
            that[collection].splice(0, that[collection].length);
            let len = obj[collection].length;
            for (let i = 0; i < len; i++) {
              let foreignObj = this.createOrUpdate(obj[collection][i], oneToMany[collection][0]);
              if (!that[collection].includes(foreignObj)) {
                that[collection].push(foreignObj);
              }
              if (oneToMany[collection][1]) {
                foreignObj[oneToMany[collection][1]] = that;
              }
            }
          }
        }
      }
      for (let collection in manyToMany) {
        if (obj[collection] !== undefined) {
          if (obj[collection] && obj[collection].length > 0) {
            that[collection].splice(0, that[collection].length);
            let len = obj[collection].length;
            for (let i = 0; i < len; i++) {
              let foreignObj = this.createOrUpdate(obj[collection][i], manyToMany[collection][0]);
              if (!that[collection].includes(foreignObj)) {
                that[collection].push(foreignObj);
              }
              if (manyToMany[collection][1]) {
                let foreignCollection = foreignObj[manyToMany[collection][1]];
                if (!foreignCollection.includes(that)) {
                  foreignCollection.push(that);
                }
              }
            }
          }
        }
      }
      return that;
    },
    get(id, constructor) {
      if (this.hasOwnProperty(constructor.name)) {
        let index = this[constructor.name].findIndex(o => o.id === id);
        if (index !== -1) {
          return this[constructor.name][index];
        }
      }
      return -1;
    }
  };
  this.Store.clear();

  this.onLoad = (loading) => {
  };

  this.onError = (json, xhttp) => {
    if (typeof json === "string") {
      console.log(xhttp);
      alert("ERREUR INTERNE\n\nToutes nos excuses, une erreur s'est produite sur nos serveurs.\nVeuillez réessayer ou contacter un administrateur si l'erreur persiste.");
    } else {
      console.log(xhttp);
      alert("ERREUR : " + json.title + "\n\n" + Message.parse(json));
    }
  }
  let url = anUrl;

  let xhttp;

  this.abort = () => {
    xhttp.abort();
  }
  let ajaxCall = ({ method, path, headers, onSuccess, onError, onProgress, data, json = true, async = true, loading = true } = {}) => {
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = () => {
      if (xhttp.readyState === 4 && (xhttp.status === 200 || xhttp.status === 204)) {
        let arr = xhttp.getAllResponseHeaders().trim().split(/[\r\n]+/);
        let headersMap = {};
        arr.forEach(function (line) {
          let parts = line.split(': ');
          let header = parts.shift();
          let value = parts.join(': ');
          headersMap[header] = value;
        });
        if (onSuccess) {
          let returnValue;
          try {
            returnValue = JSON.parse(xhttp.responseText);
          } catch (e) {
            returnValue = xhttp.responseText;
          }
          onSuccess(returnValue, headersMap);
        }
        if (loading) {
          this.onLoad(false);
        }
      } else if (xhttp.readyState === 3) {
        if (onProgress) {
          let newResponses = xhttp.responseText.split("\ndata: ");
          let response = newResponses[newResponses.length - 1];
          try {
            onProgress(JSON.parse(response));
          } catch (ex) {
            onProgress(response);
          }
        }
      } else if (xhttp.readyState === 4) {
        let response = xhttp.responseText;
        let jsonResponse = {};
        if (onProgress) {
          let newResponses = response.split("\ndata: ");
          response = newResponses[newResponses.length - 1];
        }
        try {
          jsonResponse = JSON.parse(response);
        } catch (e) {
          jsonResponse = response;
        }
        if (onError) {
          onError(jsonResponse, xhttp);
        } else {
          this.onError(jsonResponse, xhttp);
        }
        if (loading) {
          this.onLoad(false);
        }
      }
    };

    xhttp.open(method, url + "/" + path, async);
    for (let header in headers) {
      xhttp.setRequestHeader(header.name, header.value);
    }
    xhttp.setRequestHeader('Accept-Language', locale);
    if (json) {
      xhttp.setRequestHeader('Content-type', 'application/json');
      if (typeof data === "string") {
        xhttp.send(data);
      } else {
        xhttp.send(JSON.stringify(decycle(data)));
      }
    } else {
      xhttp.send(data);
    }
    if (loading) {
      this.onLoad(true);
    }
  };

  this.import = ({ data, onSuccess, onError, onProgress, loading } = {}) => {
    ajaxCall({
      method: "POST",
      path: "aaf/import",
      data: data,
      onProgress,
      onSuccess,
      onError: onError,
      json: false,
      loading: loading
    });
  };
  let parse = function (obj, className) {
    if (Array.isArray(obj)) {
      return obj.map(obj => self.Store.createOrUpdate(obj, className));
    } else {
      return self.Store.createOrUpdate(obj, className);
    }
  };
  let decycle = function (obj, root = true) {
    if (!obj || typeof obj !== 'object')
      return obj;

    if (!root && typeof obj.id !== "undefined")
      return { 'id': obj.id };

    if (obj.constructor === Date)
      return obj;

    if (Array.isArray(obj))
      return obj.map((el) => decycle(el, false));

    let replica = {};
    Object.keys(obj).forEach((key) => replica[key] = decycle(obj[key], false));
    return replica;
  };
  let EntityProcessor = function (resourcePath, className) {
    this.call = ({ method, data = null, path = "", onSuccess, onError, onProgress, query = {}, parsingClassName = className, async = true, loading = true } = {}) => {
      let urlParameters = Object.keys(query)
        .map((key) => key + '=' + encodeURIComponent(query[key]))
        .join('&');
      ajaxCall({
        method,
        data,
        path: resourcePath + path + (urlParameters.length > 0 ? "?" + urlParameters : ""),
        onSuccess: function (response) {
          let result = response;
          if (response && parsingClassName) {
            result = parse(response, parsingClassName);
          }
          onSuccess ? onSuccess(result) : null;
        },
        onError,
        onProgress,
        async,
        loading
      });
    }

    this.getAll = (params = {}) => {
      params.method = "GET";
      this.call(params);
    };
    this.get = (params = {}) => {
      params.method = "GET";
      params.path = "/" + params.id;
      this.call(params);
    };
    this.remove = (params = {}) => {
      params.method = "DELETE";
      params.path = "/" + params.data.id;
      let onSuccess = params.onSuccess;
      params.onSuccess = function () {
        self.Store.remove(params.data);
        onSuccess ? onSuccess() : null;
      };
      this.call(params);
    };
    this.create = (params = {}) => {
      params.method = "POST";
      this.call(params);
    };
    this.edit = (params = {}) => {
      params.method = "PUT";
      params.path = "/" + params.data.id;
      this.call(params);
    };
    this.createOrUpdate = (params = {}) => {
      if (params.data.id) {
        this.edit(params);
      } else {
        this.create(params);
      }
    };
  };


  this.Documentation = function ({ onSuccess, onError } = {}) {
    ajaxCall({
      method: "GET",
      path: "documentation",
      onSuccess,
      onError
    });
  };

  let Listener;
  if (!!window.SharedWorker) {
    Listener = function (aPath, className) {
      this.listen = ({ id, onMessage } = {}) => {
        let sseWorker = new SharedWorker('assets/js/service/worker.js');
        let eventPath = url + "/events/" + aPath + (id ? "/" + id : "");
        sseWorker.port.postMessage(eventPath);
        sseWorker.port.addEventListener("message", (message) => {
          let result = parse(JSON.parse(message.data.data), className);
          if (onMessage) {
            onMessage(result);
          }
        }, false);
        sseWorker.port.start();
      };
    };
  } else {
    Listener = function (aPath, className) {
      let eventPath = url + "/events/" + aPath;
      let source = null;
      this.listen = ({ id, onMessage } = {}) => {
        source = new EventSource(eventPath + (id ? "/" + id : ""));
        source.onmessage = (message) => {
          let result = parse(JSON.parse(message.data), className);
          if (onMessage) {
            onMessage(result);
          }
        };
        source.onclose = () => {
        };
      };
      this.stop = () => {
        if (source) {
          source.close();
        }
      };
    };
  }

  this.Categories = new EntityProcessor("categories", Category);
  this.Documents = new EntityProcessor("documents", Document);
  this.Debates = new EntityProcessor("debates", Debate);
  this.Teams = new EntityProcessor("teams", Team);
  this.Comments = new EntityProcessor("comments", Comment);
  this.Institutions = new EntityProcessor("institutions", Institution);
  this.Users = new EntityProcessor("users", User);
  this.Notifications = new EntityProcessor("notifications", Notification);
  this.Configurations = new EntityProcessor("configurations", Configuration);
  this.CommentListener = new Listener("comments", Comment);
  this.NotificationListener = new Listener("notifications", Notification);

  this.Users.getLoged = function (params = {}) {
    params.method = "GET";
    params.path = "/me";
    this.call(params);
  };
  this.Users.login = function (params = {}) {
    params.method = "POST";
    params.path = "/login";
    params.parsingClassName = false;
    let onSuccess = params.onSuccess;
    params.onSuccess = (token) => {
      document.cookie = "Authorization=" + token + "; maxAge=" + (params.data.rememberMe ? (360 * 24 * 60 * 60) : -1) + "; path=/";
      document.location.reload(true);
      onSuccess ? onSuccess() : null;
    }
    this.call(params);
  };
  this.Users.logout = function (params = {}) {
    params.method = "POST";
    params.path = "/logout";
    params.parsingClassName = false;
    let onSuccess = params.onSuccess;
    params.onSuccess = () => {
      document.cookie = "Authorization=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
      document.location.replace("");
      onSuccess ? onSuccess() : null;
    }
    this.call(params);
  };
  this.Users.passwd = function (params = {}) {
    params.method = "PUT";
    params.path = "/passwd";
    this.call(params);
  };
  this.Users.activate = function (params = {}) {
    params.method = "GET";
    params.path = "/activate";
    this.call(params);
  };
  this.Users.resetPasswd = function (params = {}) {
    params.method = "POST";
    params.path = "/resetPasswd";
    this.call(params);
  };
  this.Users.permanentRemove = function (params = {}) {
    params.method = "DELETE";
    params.path = "/" + params.data.id + "/permanent/";
    this.call(params);
  };
  this.Users.exists = function (params = {}) {
    params.method = "GET";
    params.path = "/test";
    params.query = { identifier: params.data };
    params.parsingClassName = false;
    this.call(params);
  };
  this.Documents.duplicate = function (params = {}) {
    params.method = "POST";
    params.path = "/" + params.id + "/duplicate";
    this.call(params);
  };
  this.Debates.addComment = function (params = {}) {
    params.method = "POST";
    params.path = "/" + params.id + "/comments";
    params.parsingClassName = Comment;
    this.call(params);
  };
  this.Debates.addTeam = function (params = {}) {
    params.method = "PUT";
    params.path = "/" + params.id + "/teams/" + params.data.id;
    let onSuccess = params.onSuccess;
    params.onSuccess = function () {
      self.Store.get(params.id, Debate).teams.push(params.data);
      params.data.debates.push(self.Store.get(params.id, Debate));
      onSuccess ? onSuccess() : null;
    };
    params.parsingClassName = Team;
    this.call(params);
  };
  this.Debates.removeTeam = function (params = {}) {
    params.method = "DELETE";
    params.path = "/" + params.id + "/teams/" + params.data.id;
    let onSuccess = params.onSuccess;
    params.onSuccess = function () {
      self.Store.get(params.id, Debate).teams.remove(params.data);
      onSuccess ? onSuccess() : null;
    };
    this.call(params);
  };
  this.Debates.addGuest = function (params = {}) {
    params.method = "PUT";
    params.path = "/" + params.id + "/users/" + params.data.id;
    let onSuccess = params.onSuccess;
    params.onSuccess = function () {
      self.Store.get(params.id, Debate).guests.push(params.data);
      params.data.invitedDebates.push(self.Store.get(params.id, Debate));
      onSuccess ? onSuccess() : null;
    };
    params.parsingClassName = User;
    this.call(params);
  };
  this.Debates.removeGuest = function (params = {}) {
    params.method = "DELETE";
    params.path = "/" + params.id + "/users/" + params.data.id;
    let onSuccess = params.onSuccess;
    params.onSuccess = function () {
      self.Store.get(params.id, Debate).guests.remove(params.data);
      onSuccess ? onSuccess() : null;
    };
    this.call(params);
  };
  this.Debates.getScraps = function (params = {}) {
    params.method = "GET";
    params.path = "/" + params.id + "/scraps";
    params.parsingClassName = false;
    this.call(params);
  };
  this.Debates.getTheme = function (params = {}) {
    params.method = "GET";
    params.path = "/" + params.id + "/theme";
    params.parsingClassName = false;
    this.call(params);
  };
  this.Teams.addUser = function (params = {}) {
    params.method = "PUT";
    params.path = "/" + params.id + "/users/" + params.data.id;
    let onSuccess = params.onSuccess;
    params.onSuccess = function () {
      self.Store.get(params.id, Team).users.push(params.data);
      params.data.teams.push(self.Store.get(params.id, Team));
      onSuccess ? onSuccess() : null;
    };
    params.parsingClassName = User;
    this.call(params);
  };
  this.Teams.removeUser = function (params = {}) {
    params.method = "DELETE";
    params.path = "/" + params.id + "/users/" + params.data.id;
    let onSuccess = params.onSuccess;
    params.onSuccess = function () {
      self.Store.get(params.id, Team).users.remove(params.data);
      onSuccess ? onSuccess() : null;
    };
    this.call(params);
  };
  this.Comments.getScalar = function (params = {}) {
    params.method = "POST";
    params.path = "/scalar";
    params.parsingClassName = false;
    this.call(params);
  };
  this.Comments.updateTags = function (params = {}) {
    params.method = "PUT";
    params.path = "/" + params.id + "/updateTags";
    params.parsingClassName = false;
    this.call(params);
  };
  this.Comments.updateAllTags = function (params = {}) {
    params.method = "PUT";
    params.path = "/updateTags";
    params.parsingClassName = false;
    this.call(params);
  };

  this.Notifications.readAll = function (params = {}) {
    params.method = "PUT";
    this.call(params);
  };

  this.Configurations.getAll = function (params = {}) {
    params.method = "GET";
    let onSuccess = params.onSuccess;
    params.onSuccess =  function (result) {
      self.Configs = {}
      for (const prop of result) {
        self.Configs[prop.key] = prop.value == 'true' ? true : prop.value == 'false' ? false : prop.value;
      }
      onSuccess ? onSuccess(result) : null;
    }
    this.call(params);
  };

  this.Configurations.editAll = function (params = {}) {
    params.method = "PUT";
    params.data = Object.entries(params.data).map(e => ({key: e[0], value: e[1]}));
    let onSuccess = params.onSuccess;
    params.onSuccess = function (result) {
      self.Configs = {}
      for (const prop of result) {
        self.Configs[prop.key] = prop.value == 'true' ? true : prop.value == 'false' ? false : prop.value;
      }
      onSuccess ? onSuccess(result) : null;
    }
    this.call(params);
  };
};