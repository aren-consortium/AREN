<template>
    <div id="toast-container">
        <transition-group @enter="enter">
            <div v-for="toast in toasts"
                 v-bind:key="toast.id"
                 class="toast"
                 style="top: 0px; opacity: 1;"
                 v-html="toast.html">
            </div>
        </transition-group>
    </div>
</template>

<script>
    module.exports = {
        props: {
            displayLength: {
                default: 4000
            }
        },
        data() {
            return {
                idInc: 0,
                toasts: []
            };
        },
        mounted() {
            if (!Vue.prototype.$toast) {
                Vue.prototype.$toast = (html) => {
                    this.toasts.push({id: this.idInc, html: html});
                    this.idInc++;
                }
            } else {
                throw "This is a singleton component you cannot have many of if in the same app";
            }
        },
        methods: {
            enter(el) {
                setTimeout(() => this.toasts.splice(0, 1), this.displayLength);
            }
        }
    };
</script>