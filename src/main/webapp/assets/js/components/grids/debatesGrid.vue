<template>
    <grid-layout
        v-bind:extendable="extendable"
        v-bind:columns="[ 'min-content', '2fr', '1fr', '2fr', '3fr', '2fr', '2fr' ]"
        v-bind:headers="[ $t('category'), $t('restitution'), $t('posts'), $t('last_post'), $t('document'), $t('teams'), $t('moderators') ]"
        v-bind:actions="[ 2, 2, 2 ]"
        v-bind:items="categories"
        v-bind:grouped-items="( group ) => group.debates()"
        v-bind:sort="( a, b ) => a.created - b.created"
        @selection-change="$emit('selection-change', $event)">

        <template v-slot:row.group="{ value: category }">
            <div class="card center-align">
                <div class="card-image light-color valign-wrapper">
                    <img v-bind:src="category.picture">
                </div>
                <div>
                    <h2 v-bind:title="category.name">{{ category.name }}</h2>
                </div>
            </div>
        </template>

        <template v-if="$scopedSlots['side.actions']" v-slot:side.actions="{ value: debate }">
            <slot name="side.actions" v-bind:debate="debate"></slot>
        </template>

        <template v-slot:column.1="{ value: debate }">
            <div v-if="debate.commentsCount > 0" class="opinion">
                <div class="for" v-bind:style="'width: '+ (debate.commentsCountFor / debate.commentsCount * 100) + '%;'"></div>
                <div class="against" v-bind:style="'width: ' + (debate.commentsCountAgainst / debate.commentsCount * 100) + '%;'"></div>
            </div>
        </template>

        <template v-slot:column.2="{ value: debate }">
            {{ debate.commentsCount }}
        </template>

        <template v-slot:column.3="{ value: debate }">
            {{ debate.lastCommentDate !== 0 ? $d(debate.lastCommentDate, 'short') : $t('no_post') }}
        </template>

        <template v-slot:column.4="{ value: debate }">
            {{ debate.document.name }}
        </template>

        <template v-slot:column.5="{ value: debate }">
            <div v-for="team in debate.teams" v-bind:key="team.id">
                {{ team.name }}<br>
            </div>
        </template>

        <template v-slot:column.6="{ value: debate }">
            <div v-for="guest in debate.guests" v-bind:key="guest.id">
                {{ guest.fullName() }}<br>
            </div>
        </template>

        <template v-slot:action.1="{ value: debate }">
            <router-link v-bind:to="debate.id+'/restitution'" append class="waves-effect waves-light btn">Restitution</router-link>
        </template>

        <template v-slot:action.2="{ value: debate }">
            <router-link v-bind:to="''+debate.id" append class="waves-effect waves-light btn">Aller au débat</router-link>
        </template>

        <template v-slot:action.3="{ value: debate }">
            <slot name="add.contributor" v-bind:debate="debate"></slot>
        </template>

    </grid-layout>
</template>

<script>
    module.exports = {
        mixins: [VueGrid],
        props: ['categories', 'extendable']
    };
</script>