<template>
    <div v-bind:class="{ grid: true, striped: striped }"
         v-bind:style="'grid-template-columns: ' + columnsLayout">
        <div class="ghead">
            <div class="gr">
                <div class="gh" v-for="(header, index) in headers" v-bind:key="index">{{ header }}</div>
                <div class="gh" v-if="$scopedSlots['side.actions']"></div>
            </div>
        </div>

        <div class="gbody" v-if="items">
            <!-- if there is no groups, then `rows` is an array with a single element that is `items` -->
            <template v-for="(group, index) in groups"
                      v-if="pages === 0 || groupInPage(index)">
                <div v-if="index > 0" class="separator"></div>
                <div v-if="$scopedSlots['row.group']" v-bind:ref="'group'+group.item.id" class="gr header">
                    <div class="gh" v-bind:style="'grid-row: span ' + (group.children.length * 2 + 3)">
                        <slot name="row.group" v-bind:value="group.item"></slot>
                    </div>
                </div>

                <div class="gr" v-for="(item, rowNum) in group.children"
                     v-if="pages === 0 || rowInPage(index, rowNum)"
                     v-bind:key="item.id"
                     v-bind:ref="'row'+item.id"
                     v-bind:id="'row'+item.id">
                    <div class="gd" v-for="colNum in columns.length - ($scopedSlots['row.group'] ? 1 : 0)"
                         v-bind:key="colNum"
                         tabindex="0">
                        <slot v-bind:name="'column.' + colNum" v-bind:value="item"></slot>
                    </div>

                    <div v-if="$scopedSlots['side.actions']"
                         class="gd side action"
                         tabindex="0">
                        <slot v-if="!extendable" name="side.actions" v-bind:value="item"></slot>
                    </div>

                    <div v-if="extendable" class="gr extendable">
                        <div v-for="(action, actNum) in actions"
                             v-bind:key="actNum"
                             class="gd action"
                             tabindex="0"
                             v-bind:style="'grid-column: span ' + action">
                            <slot v-bind:name="'action.' + (actNum + 1)" v-bind:value="item"></slot>
                        </div>
                        <div v-if="$scopedSlots['side.actions']"
                             tabindex="0"
                             class="gd side action">
                            <slot name="side.actions" v-bind:value="item"></slot>
                        </div>
                    </div>
                </div>
            </template>
        </div>

        <ul v-if="pages > 1" class="pagination">
            <li v-bind:class="page === 0 ? 'disabled' : 'waves-effect'">
                <a @click="page > 0 ? page-- : ''"><i class="material-icons">chevron_left</i></a>
            </li>
            <li v-for="n in pages"
                v-bind:class="{'waves-effect':true, active: page === (n-1)}">
                <a @click="page = (n-1)">{{ n }}</a>
            </li>
            <li v-bind:class="page === pages-1 ? 'disabled' : 'waves-effect'">
                <a @click="page < pages-1 ? page++ : ''"><i class="material-icons">chevron_right</i></a>
            </li>
        </ul>

    </div>
</template>

<script>
    module.exports = {
        data() {
            return {
                page: 0
            }
        },
        props: {
            extendable: {type: Boolean, default: false},
            striped: {type: Boolean, default: true},
            columns: {type: Array, required: true},
            headers: Array,
            items: Array,
            actions: Array,
            groupedItems: Function,
            filter: Function,
            sort: Function,
            pagination: {type: Number, default: 20},
        },
        computed: {
            groups( ) {
                let groups = [];
                if (this.groupedItems) {
                    groups = this.items.map((group) => {
                        return {item: group, children: this.filterAndSort(this.groupedItems(group))}
                    });
                    this.filterAndSort(groups);
                } else {
                    groups = [{item: {}, children: this.filterAndSort(this.items)}]
                }
                return groups;
            },
            pages() {
                if (this.pagination != -1) {
                    return Math.ceil(this.groups.reduce((acc, cur) => acc += cur.children.length, 0) / this.pagination);
                }
                return 0;
            },
            pageStart() {
                return this.page * this.pagination;
            },
            pageEnd() {
                return (this.page + 1) * this.pagination;
            },
            columnsLayout() {
                let layout = this.columns.join(' ');
                if (this.$scopedSlots['side.actions']) {
                    layout += ' min-content';
                }
                return layout;
            }
        },
        watch: {
            pages() {
                this.page = 0;
            }
        },
        methods: {
            filterAndSort(items) {
                let its = items;
                if (this.filter) {
                    its = its.filter(this.filter);
                }
                if (this.sort) {
                    its = its.sort(this.sort);
                }
                return its;
            },
            groupInPage(index) {
                let pageIndex = this.groups.slice(0, index).reduce((acc, cur) => acc += cur.children.length, 0);
                let pageNextIndex = pageIndex + this.groups[index].children.length;
                return pageIndex < this.pageEnd && pageNextIndex >= this.pageStart;
            },
            rowInPage(gIndex, index) {
                let pageIndex = this.groups.slice(0, gIndex).reduce((acc, cur) => acc += cur.children.length, 0) + index;
                return pageIndex >= this.pageStart && pageIndex < this.pageEnd;
            }
        }
    };
</script>