package com.nrikesari.app.viewmodel

import com.nrikesari.app.model.PortfolioProject
import com.nrikesari.app.model.Service
import com.nrikesari.app.model.TeamMember

object MockDataRepository {
    fun getServices(): List<Service> = listOf(
        Service("1", "Video Editing", "Professional video editing for YouTube, brands, ads, and cinematic content.", benefits = listOf("High retention rates", "Cinematic color grading", "Fast turnaround"), whyChooseUs = "We have edited for top channels and brands resulting in millions of views."),
        Service("2", "3D / VFX", "3D animation, CGI, motion graphics, product visualization, and visual effects.", benefits = listOf("Photorealistic renders", "Creative animation", "Product visualization"), whyChooseUs = "Our artistic touch makes the impossible look real."),
        Service("3", "Graphic Design", "Logo design, posters, social media creatives, brand identity kits.", benefits = listOf("Brand consistency", "Eye-catching visuals", "Modern aesthetics"), whyChooseUs = "We build visual identities that command attention."),
        Service("4", "UI/UX Design", "Modern interfaces, app design, wireframes, UX strategy.", benefits = listOf("Higher conversion", "Intuitive flows", "Accessible design"), whyChooseUs = "User-centric approach mixed with premium aesthetics."),
        Service("5", "Web Development", "Business websites, e-commerce platforms, custom web applications.", benefits = listOf("Blazing fast load times", "SEO optimized", "Scalable architecture"), whyChooseUs = "We build the digital real estate of tomorrow."),
        Service("6", "App Development", "Android and iOS app development with scalable backend.", benefits = listOf("Native experience", "Offline capabilities", "Secure data handling"), whyChooseUs = "We develop apps that feel luxurious and responsive."),
        Service("7", "Digital Marketing", "SEO, paid ads, branding strategy, performance campaigns.", benefits = listOf("Higher ROI", "Targeted reach", "Data-driven decisions"), whyChooseUs = "We don't just spend money, we invest it for growth.")
    )

    fun getPortfolio(): List<PortfolioProject> = listOf(
        PortfolioProject("1", "FinTech App Rebrand", "App Projects", "Complete redesign of a legacy finance app.", "User retention increased by 40%", ""),
        PortfolioProject("2", "Luxury Watch E-commerce", "Web Projects", "High-end website for a boutique watchmaker.", "Sales grew by 200% in Q1", ""),
        PortfolioProject("3", "Cyberpunk Energy Drink", "VFX / 3D", "A fully CGI commercial for a new startup.", "Over 5M views on social media", ""),
        PortfolioProject("4", "Minimalist Skincare Brand", "Branding", "Brand identity from scratch including packaging.", "Featured in top design magazines", ""),
        PortfolioProject("5", "Holiday Mega Sale Ad", "Marketing Campaigns", "Performance marketing campaign for local retailer.", "10x ROAS achieved", "")
    )

    fun getTeam(): List<TeamMember> = listOf(
        TeamMember("1", "Venkata", "Founder", ""),
        TeamMember("2", "John", "Developer", ""),
        TeamMember("3", "Lisa", "Designer", ""),
        TeamMember("4", "Mike", "Marketing Strategist", "")
    )
}
