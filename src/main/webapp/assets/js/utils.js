function decycle(obj, stack = [ ]) {
    if (!obj || typeof obj !== 'object')
        return obj;

    if (stack.includes(obj))
        return null;

    let s = stack.concat([obj]);

    return Array.isArray(obj) ? obj.map(x => decycle(x, s))
            : Object.assign(new obj.constructor(), Object.fromEntries(
                    Object.entries(obj)
                    .map(([k, v]) => [k, decycle(v, s)])));
}

function vueLoader(file) {
    return httpVueLoader('assets/js/' + file + '.vue');
}

/***** Useful function on prototypes *****/
Array.prototype.remove = function (obj) {
    let index = this.indexOf(obj);
    if (index !== -1) {
        this.splice(index, 1);
        return true;
    } else {
        return false;
    }
};

Array.prototype.compare = function (obj) {
    if (this === obj) {
        return 0;
    }
    let len = Math.max(this.length, obj.length);
    for (let i = 0; i < len; i++) {
        if (this[i] === undefined || obj[i] !== undefined && this[i] < obj[i]) {
            return -1;
        } else if (obj[i] === undefined || this[i] !== undefined && this[i] > obj[i]) {
            return 1;
        }
    }
    return 0;
}

if (!Array.prototype.includes) {
    Array.prototype.includes = function (obj) {
        return this.indexOf(obj) !== -1;
    }
}

if (!String.prototype.includes) {
    String.prototype.includes = function (obj) {
        return this.indexOf(obj) !== -1;
    }
}

Node.prototype.getChildPathTo = function (el) {
    if (el === this) {
        return "";
    }
    let sibIndex = -1;
    let len = el.parentNode.childNodes.length;
    let sib;
    while (sibIndex < len && sib !== el) {
        sibIndex++;
        sib = el.parentNode.childNodes[sibIndex];
    }
    if (el.parentNode === this) {
        return sibIndex;
    } else {
        return this.getChildPathTo(el.parentNode) + "/" + sibIndex;
    }
};

Node.prototype.childPathSelector = function (path) {
    if (path.constructor !== String) {
        path = path + "";
    }
    let arrayPath = path.split("/");
    var result = this;
    let len = arrayPath.length;
    for (let i = 0; i < len; i++) {
        if (result.childNodes.length > arrayPath[i]) {
            result = result.childNodes[arrayPath[i]];
        } else {
            console.warn("Warning : impossible to find child number " + arrayPath[i] + ". The return child may be wrong.", this, result);
        }
    }
    return result;
};

Node.prototype.forEachNode = function (callback, origin = this, end = false, index = []) {
    if (end && this === origin) {
        return null;
    }
    if (!end && callback) {
        callback(this, [...index], origin);
    }
    if (!end && this.firstChild !== null) {
        index.push(0);
        this.firstChild.forEachNode(callback, origin, false, index);
    } else if (this.nextSibling !== null) {
        index[index.length - 1]++;
        this.nextSibling.forEachNode(callback, origin, false, index);
    } else {
        index.pop();
        this.parentNode.forEachNode(callback, origin, true, index);
}
}

/***** Selection handling *****/
function compareRange(type, r1, r2) {
    let pos1, pos2;
    switch (type) {
        case Range.START_TO_START:
            pos1 = (r1.startContainer + "/" + r1.startOffset).split("/").map(v => v * 1);
            pos2 = (r2.startContainer + "/" + r2.startOffset).split("/").map(v => v * 1);
            break;
        case Range.START_TO_END:
            pos1 = (r1.startContainer + "/" + r1.startOffset).split("/").map(v => v * 1);
            pos2 = (r2.endContainer + "/" + r2.endOffset).split("/").map(v => v * 1);
            break;
        case Range.END_TO_START:
            pos1 = (r1.endContainer + "/" + r1.endOffset).split("/").map(v => v * 1);
            pos2 = (r2.startContainer + "/" + r2.startOffset).split("/").map(v => v * 1);
            break;
        case Range.END_TO_END:
            pos1 = (r1.endContainer + "/" + r1.endOffset).split("/").map(v => v * 1);
            pos2 = (r2.endContainer + "/" + r2.endOffset).split("/").map(v => v * 1);
            break;
        default:
            throw "Invalid first parameter. Use Range.START_TO_START, Range.START_TO_END, Range.END_TO_START or Range.END_TO_END.";
    }
    return pos1.compare(pos2);
}

Range.prototype.setPathStart = function (container, startContainer, startOffset) {
    this.setStart(startContainer ? container.childPathSelector(startContainer) : container.firstChild, startOffset);
}

Range.prototype.setPathEnd = function (container, endContainer, endOffset) {
    this.setEnd(endContainer ? container.childPathSelector(endContainer) : container.firstChild, endOffset);
}

Range.prototype.getHtml = function () {
    let div = document.createElement('div');
    div.appendChild(this.cloneContents());
    return div.innerHTML;
}

Range.prototype.affineToWord = function () {
    let regExp = new RegExp('[A-Za-zÀ-ÖØ-öø-ÿ\'\.\n\t]');
    while (this.startOffset > 0 && regExp.test(this.toString().charAt(0))) {
        this.setStart(this.startContainer, this.startOffset - 1);
    }
    if (this.startOffset !== 0) {
        this.setStart(this.startContainer, this.startOffset + 1);
    }
    while (this.endOffset < this.endContainer.length && regExp.test(this.toString().slice(-1))) {
        this.setEnd(this.endContainer, this.endOffset + 1);
    }
    if (!regExp.test(this.toString().slice(-1)) && this.endOffset !== 0) {
        this.setEnd(this.endContainer, this.endOffset - 1);
    }
}

function clearSelection() {
    if (window.getSelection) {
        window.getSelection().removeAllRanges();
    } else if (document.selection) {
        document.selection.empty();
    }
}


/***** Smooth Scroll *****/
var scrollProcess = [];

Math.easeOutExpo = function (time, origin, delta, duration) {
    return delta * (-Math.pow(2, -10 * time / duration) + 1) + origin;
};

function clearAllSmoothScroll(container) {
    let index = scrollProcess.findIndex((s) => s.container === container);
    if (index !== -1) {
        let len = scrollProcess[index].intervals.length;
        for (var i = 0; i < len; i++) {
            clearInterval(scrollProcess[index].intervals[i]);
        }
        scrollProcess.splice(index, 1);
    }
}

function smoothScroll(container, scrollGoal, callback, duration = 0.5) {
    clearAllSmoothScroll(container);

    let scrollDir = container.scrollTop < scrollGoal ? 1 : -1;
    let timeInt = 5;

    let time = 0;
    let origin = container.scrollTop;
    let delta = scrollGoal - container.scrollTop;
    duration = duration * (1000 / timeInt);

    let index = scrollProcess.findIndex((s) => s.container === container);
    if (index === -1) {
        scrollProcess.push({
            container,
            intervals: []
        });
        index = scrollProcess.length - 1;
    }

    scrollProcess[index].intervals.push(setInterval(() => {
        if (time === duration
                || scrollDir === 1 && (
                        container.scrollTop === container.scrollTopMax
                        || container.scrollTop >= scrollGoal - 1)
                || scrollDir === -1 && (
                        container.scrollTop === 0
                        || container.scrollTop <= scrollGoal + 1)
                ) {
            clearAllSmoothScroll(container);
            if (callback) {
                callback();
            }
        } else {
            container.scrollTop = Math.easeOutExpo(time, origin, delta, duration);
            time++;
        }
    }, timeInt));
}

function scrollCenter(container, element, callback, duration = 0.5) {
    let elemRect = element.getBoundingClientRect();
    let containerRect = container.getBoundingClientRect();
    let scrollGoal = elemRect.top + (container.scrollTop - containerRect.top);
    smoothScroll(container, scrollGoal - (containerRect.height / 4), callback, duration);
}

function inScrollView(container, element) {
    let elemRect = element.getBoundingClientRect();
    let containerRect = container.getBoundingClientRect();
    let scrollTop = elemRect.top + (container.scrollTop - containerRect.top);
    let scrollBottom = elemRect.top + (container.scrollTop - containerRect.top - containerRect.height) + elemRect.height;
    return (container.scrollTop < scrollTop && container.scrollTop > scrollBottom);
}

/***** Color function *****/
const HSL = {
    fromString(color) {
        let match = /hsl\((\d+),\s*([\d.]+)%,\s*([\d.]+)%\)/g.exec(color);
        return {h: match[1], s: match[2], l: match[3] / 100};
    },
    toString(hsl) {
        return "hsl(" + hsl.h + ", " + hsl.s * 100 + "%, " + hsl.l * 100 + "%)";
    }
}